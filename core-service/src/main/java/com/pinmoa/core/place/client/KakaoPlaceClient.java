package com.pinmoa.core.place.client;

import com.pinmoa.core.place.dto.KakaoPlaceResult;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoPlaceClient {

    private final RestTemplate restTemplate;

    @Value("${kakao.api.key}")
    private String apiKey;

    private static final String KAKAO_SEARCH_URL =
        "https://dapi.kakao.com/v2/local/search/keyword.json";

    public List<KakaoPlaceResult> search(String query) {
        var uri = UriComponentsBuilder.fromUriString(KAKAO_SEARCH_URL)
            .queryParam("query", "{query}")
            .queryParam("size", 15)
            .buildAndExpand(query)
            .encode()
            .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);

        Map<String, Object> response = restTemplate.exchange(
            uri,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Map<String, Object>>() {}
        ).getBody();

        log.info("Kakao API response: {}", response);
        if (response == null || !response.containsKey("documents")) {
            log.warn("Kakao API returned no documents. response={}", response);
            return List.of();
        }

        List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
        return documents.stream()
            .map(doc -> new KakaoPlaceResult(
                (String) doc.get("id"),
                (String) doc.get("place_name"),
                (String) doc.get("category_name"),
                (String) doc.get("address_name"),
                (String) doc.get("x"),
                (String) doc.get("y"),
                (String) doc.get("place_url")
            ))
            .toList();
    }
}
