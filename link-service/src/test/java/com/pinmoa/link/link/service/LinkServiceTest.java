package com.pinmoa.link.link.service;

import com.pinmoa.link.ai.dto.ExtractedPlace;
import com.pinmoa.link.ai.service.PlaceTextExtractor;
import com.pinmoa.link.global.exception.LinkProcessingException;
import com.pinmoa.link.link.client.InstagramCaptionExtractor;
import com.pinmoa.link.link.client.KakaoPlaceSearchClient;
import com.pinmoa.link.link.dto.InstaResult;
import com.pinmoa.link.link.dto.LinkExtractRequest;
import com.pinmoa.link.link.dto.LinkExtractResponse;
import com.pinmoa.link.link.dto.PlaceCandidate;
import com.pinmoa.link.link.repository.LinkRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

	@Mock
	private InstagramCaptionExtractor instagramCaptionExtractor;
	@Mock
	private PlaceTextExtractor placeTextExtractor;
	@Mock
	private KakaoPlaceSearchClient kakaoPlaceSearchClient;
	@Mock
	private LinkRepository linkRepository;

	@InjectMocks
	private LinkService linkService;

	private PlaceCandidate candidate(String id, String name) {
		return new PlaceCandidate(id, name, "음식점", "서울 성동구", "서울 성동구 연무장길",
			37.5, 127.0, "http://place.map.kakao.com/" + id, "02-000-0000");
	}

	@DisplayName("링크에서 추출한 장소를 카카오 검색해 후보로 반환한다")
	@Test
	void extractReturnsCandidates() {
		when(instagramCaptionExtractor.fetch(any()))
			.thenReturn(new InstaResult("성수동 미오 파스타 맛집", false, null));
		when(placeTextExtractor.extract("성수동 미오 파스타 맛집"))
			.thenReturn(List.of(new ExtractedPlace("미오 성수", "성수동")));
		when(kakaoPlaceSearchClient.search(any()))
			.thenReturn(List.of(candidate("1", "미오 성수")));

		LinkExtractResponse response = linkService.extract(
			1L, new LinkExtractRequest("https://www.instagram.com/reel/abc"));

		assertThat(response.platform()).isEqualTo("INSTAGRAM");
		assertThat(response.candidates()).hasSize(1);
		assertThat(response.candidates().get(0).name()).isEqualTo("미오 성수");
	}

	@DisplayName("여러 장소 검색 결과 중 kakaoPlaceId가 중복되면 제거한다")
	@Test
	void deduplicatesByKakaoPlaceId() {
		when(instagramCaptionExtractor.fetch(any()))
			.thenReturn(new InstaResult("desc", false, null));
		when(placeTextExtractor.extract(any()))
			.thenReturn(List.of(new ExtractedPlace("미오 성수", "성수동"),
				new ExtractedPlace("미오", "성수")));
		// 두 검색 모두 동일한 id "1" 후보를 포함
		when(kakaoPlaceSearchClient.search(any()))
			.thenReturn(List.of(candidate("1", "미오 성수"), candidate("2", "센터커피")));

		LinkExtractResponse response = linkService.extract(
			1L, new LinkExtractRequest("https://www.instagram.com/reel/abc"));

		assertThat(response.platform()).isEqualTo("INSTAGRAM");
		assertThat(response.candidates())
			.extracting(PlaceCandidate::kakaoPlaceId)
			.containsExactly("1", "2");
	}

	@DisplayName("kakaoPlaceId가 null인 후보는 중복 제거 없이 유지된다")
	@Test
	void keepsCandidatesWithoutId() {
		when(instagramCaptionExtractor.fetch(any()))
			.thenReturn(new InstaResult("desc", false, null));
		when(placeTextExtractor.extract(any()))
			.thenReturn(List.of(new ExtractedPlace("어딘가", "")));
		when(kakaoPlaceSearchClient.search(any()))
			.thenReturn(List.of(candidate(null, "이름없음1"), candidate(null, "이름없음2")));

		LinkExtractResponse response = linkService.extract(
			1L, new LinkExtractRequest("https://www.instagram.com/reel/abc"));

		assertThat(response.candidates()).hasSize(2);
	}

	@DisplayName("인스타그램이 아닌 링크는 거부한다")
	@Test
	void rejectsNonInstagramPlatform() {
		assertThatThrownBy(() -> linkService.extract(
			1L, new LinkExtractRequest("https://www.tiktok.com/@u/video/1")))
			.isInstanceOf(LinkProcessingException.class);
	}

	@DisplayName("LLM이 장소를 찾지 못하면 빈 후보 목록을 반환한다")
	@Test
	void emptyWhenNoPlaceExtracted() {
		when(instagramCaptionExtractor.fetch(any()))
			.thenReturn(new InstaResult("장소 없는 설명", false, null));
		when(placeTextExtractor.extract(any())).thenReturn(List.of());

		LinkExtractResponse response = linkService.extract(
			1L, new LinkExtractRequest("https://www.instagram.com/reel/none"));

		assertThat(response.candidates()).isEmpty();
	}
}
