package com.pinmoa.link.link.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinmoa.link.link.dto.PlaceCandidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 카카오 로컬 키워드 검색 API 클라이언트.
 * 장소명(키워드)으로 검색해 좌표/주소/카카오 장소 ID가 포함된 후보 목록을 반환한다.
 */
@Slf4j
@Component
public class KakaoPlaceSearchClient {

	private final RestClient restClient;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final String searchUrl;
	private final int size;

	public KakaoPlaceSearchClient(
		@Value("${kakao.api.key}") String apiKey,
		@Value("${kakao.api.local-search-url}") String searchUrl,
		@Value("${kakao.api.search-size:5}") int size
	) {
		this.searchUrl = searchUrl;
		this.size = size;
		this.restClient = RestClient.builder()
			.defaultHeader("Authorization", "KakaoAK " + apiKey)
			.build();
	}

	public List<PlaceCandidate> search(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return List.of();
		}
		try {
			String body = restClient.get()
				.uri(searchUrl + "?query={query}&size={size}", keyword, size)
				.retrieve()
				.body(String.class);

			return toCandidates(body == null ? null : objectMapper.readTree(body));
		} catch (Exception e) {
			log.warn("카카오 장소 검색 실패 (keyword={})", keyword, e);
			return List.of();
		}
	}

	private List<PlaceCandidate> toCandidates(JsonNode body) {
		List<PlaceCandidate> candidates = new ArrayList<>();
		if (body == null) {
			return candidates;
		}
		JsonNode documents = body.path("documents");
		for (JsonNode doc : documents) {
			candidates.add(new PlaceCandidate(
				doc.path("id").asText(null),
				doc.path("place_name").asText(null),
				doc.path("category_name").asText(null),
				doc.path("address_name").asText(null),
				doc.path("road_address_name").asText(null),
				parseDouble(doc.path("y").asText(null)),
				parseDouble(doc.path("x").asText(null)),
				doc.path("place_url").asText(null),
				doc.path("phone").asText(null)
			));
		}
		return candidates;
	}

	private Double parseDouble(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
