package com.pinmoa.core.user.client;

import com.pinmoa.core.global.exception.BusinessException;
import com.pinmoa.core.global.exception.ErrorCode;
import com.pinmoa.core.user.dto.KakaoUserInfo;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 모바일 앱이 카카오 SDK로 로그인해 받은 access token을 카카오 서버에 검증하고 사용자 정보를 가져온다.
 * 웹(SPA)처럼 access token 대신 인가 코드만 받은 경우를 위해 code → access token 교환도 지원한다.
 * client_secret은 브라우저에 노출하지 않기 위해 항상 이 서버에서 보관/전송한다.
 */
@Slf4j
@Component
public class KakaoUserClient {

    private static final String KAKAO_USER_ME_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;

    public KakaoUserClient(
        RestTemplate restTemplate,
        @Value("${kakao.api.key}") String clientId,
        @Value("${kakao.oauth.client-secret:}") String clientSecret
    ) {
        this.restTemplate = restTemplate;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String exchangeCodeForAccessToken(String code, String redirectUri) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", clientId);
        form.add("redirect_uri", redirectUri);
        form.add("code", code);
        if (clientSecret != null && !clientSecret.isBlank()) {
            form.add("client_secret", clientSecret);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, Object> response;
        try {
            response = restTemplate.exchange(
                KAKAO_TOKEN_URL,
                HttpMethod.POST,
                new HttpEntity<>(form, headers),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            ).getBody();
        } catch (RestClientException e) {
            log.warn("카카오 토큰 교환 실패", e);
            throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
        }

        if (response == null || response.get("access_token") == null) {
            throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
        }
        return (String) response.get("access_token");
    }

    public KakaoUserInfo getUserInfo(String kakaoAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoAccessToken);

        Map<String, Object> response;
        try {
            response = restTemplate.exchange(
                KAKAO_USER_ME_URL,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            ).getBody();
        } catch (RestClientException e) {
            log.warn("카카오 사용자 정보 조회 실패", e);
            throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
        }

        if (response == null || response.get("id") == null) {
            throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
        }

        String socialId = String.valueOf(response.get("id"));
        Map<String, Object> account = (Map<String, Object>) response.getOrDefault("kakao_account", Map.of());
        Map<String, Object> profile = (Map<String, Object>) account.getOrDefault("profile", Map.of());

        String email = (String) account.get("email");
        String nickname = (String) profile.get("nickname");
        String profileImageUrl = (String) profile.get("profile_image_url");

        return new KakaoUserInfo(
            socialId,
            email != null ? email : "kakao_" + socialId + "@kakao.pinmoa.com",
            nickname != null ? nickname : "카카오사용자",
            profileImageUrl
        );
    }
}
