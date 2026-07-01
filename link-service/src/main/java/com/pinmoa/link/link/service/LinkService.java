package com.pinmoa.link.link.service;

import com.pinmoa.link.ai.dto.ExtractedPlace;
import com.pinmoa.link.ai.service.PlaceTextExtractor;
import com.pinmoa.link.global.exception.LinkProcessingException;
import com.pinmoa.link.link.client.InstagramCaptionExtractor;
import com.pinmoa.link.link.client.KakaoPlaceSearchClient;
import com.pinmoa.link.link.domain.Platform;
import com.pinmoa.link.link.domain.SnsLink;
import com.pinmoa.link.link.dto.InstaResult;
import com.pinmoa.link.link.dto.LinkExtractRequest;
import com.pinmoa.link.link.dto.LinkExtractResponse;
import com.pinmoa.link.link.dto.PlaceCandidate;
import com.pinmoa.link.link.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 링크 → 장소 후보 추출 파이프라인 오케스트레이션. 현재는 인스타그램만 지원한다.
 * 0) 요청 이력을 sns_links 에 저장 (누가 어떤 링크를 요청했는지)
 * 1) 인스타그램 임베드 페이지(크롤러 UA)로 캡션 추출
 * 2) LLM(Gemini) 으로 캡션에서 장소 단서 추출
 * 3) 카카오맵에서 각 장소를 검색해 후보로 통합
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LinkService {

	private final InstagramCaptionExtractor instagramCaptionExtractor;
	private final PlaceTextExtractor placeTextExtractor;
	private final KakaoPlaceSearchClient kakaoPlaceSearchClient;
	private final LinkRepository linkRepository;

	@Transactional
	public LinkExtractResponse extract(Long userId, LinkExtractRequest request) {
		Platform platform = Platform.detect(request.url());
		linkRepository.save(SnsLink.create(userId, request.url(), platform));

		if (platform != Platform.INSTAGRAM) {
			throw new LinkProcessingException("지원하지 않는 플랫폼입니다: " + platform);
		}

		String caption = extractCaption(request.url());
		List<ExtractedPlace> extractedPlaces = placeTextExtractor.extract(caption);
		log.info("userId={}, 플랫폼={}, LLM 추출 장소 수={}", userId, platform, extractedPlaces.size());

		List<PlaceCandidate> candidates = searchAndMerge(extractedPlaces);
		return new LinkExtractResponse(platform.name(), candidates);
	}

	private String extractCaption(String url) {
		InstaResult result = instagramCaptionExtractor.fetch(url);
		if (result.blocked() || result.caption() == null) {
			throw new LinkProcessingException(
				"인스타그램 캡션을 가져오지 못했습니다" + (result.error() != null ? ": " + result.error() : ""));
		}
		return result.caption();
	}

	/**
	 * 추출된 각 장소를 카카오맵에서 검색하고, kakaoPlaceId 기준으로 중복을 제거해 통합한다.
	 */
	private List<PlaceCandidate> searchAndMerge(List<ExtractedPlace> extractedPlaces) {
		Map<String, PlaceCandidate> byId = new LinkedHashMap<>();
		List<PlaceCandidate> noId = new ArrayList<>();

		for (ExtractedPlace place : extractedPlaces) {
			for (PlaceCandidate candidate : kakaoPlaceSearchClient.search(place.toSearchKeyword())) {
				if (candidate.kakaoPlaceId() == null) {
					noId.add(candidate);
				} else {
					byId.putIfAbsent(candidate.kakaoPlaceId(), candidate);
				}
			}
		}

		List<PlaceCandidate> merged = new ArrayList<>(byId.values());
		merged.addAll(noId);
		return merged;
	}
}
