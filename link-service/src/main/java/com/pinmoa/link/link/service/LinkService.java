package com.pinmoa.link.link.service;

import com.pinmoa.link.ai.dto.ExtractedPlace;
import com.pinmoa.link.ai.service.PlaceTextExtractor;
import com.pinmoa.link.link.client.KakaoPlaceSearchClient;
import com.pinmoa.link.link.domain.Platform;
import com.pinmoa.link.link.dto.LinkExtractRequest;
import com.pinmoa.link.link.dto.LinkExtractResponse;
import com.pinmoa.link.link.dto.PlaceCandidate;
import com.pinmoa.link.link.dto.VideoMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 링크 → 장소 후보 추출 파이프라인 오케스트레이션.
 * 1) yt-dlp 로 동영상 description 추출
 * 2) LLM(Bedrock) 으로 description 에서 장소 단서 추출
 * 3) 카카오맵에서 각 장소를 검색해 후보로 통합
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LinkService {

	private final VideoMetadataExtractor videoMetadataExtractor;
	private final PlaceTextExtractor placeTextExtractor;
	private final KakaoPlaceSearchClient kakaoPlaceSearchClient;

	public LinkExtractResponse extract(LinkExtractRequest request) {
		Platform platform = Platform.detect(request.url());

		VideoMetadata metadata = videoMetadataExtractor.extract(request.url());
		List<ExtractedPlace> extractedPlaces = placeTextExtractor.extract(metadata.description());
		log.info("플랫폼={}, LLM 추출 장소 수={}", platform, extractedPlaces.size());

		List<PlaceCandidate> candidates = searchAndMerge(extractedPlaces);
		return new LinkExtractResponse(platform.name(), candidates);
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
