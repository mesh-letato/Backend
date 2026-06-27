package com.pinmoa.core.auth.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Slf4j
@Component
public class KakaoTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final RestClient restClient = RestClient.create();

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest request) {
        log.info("[KakaoTokenResponseClient] getTokenResponse 호출됨");
        String tokenUri = request.getClientRegistration().getProviderDetails().getTokenUri();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", request.getClientRegistration().getClientId());
        params.add("redirect_uri", request.getAuthorizationExchange().getAuthorizationRequest().getRedirectUri());
        params.add("code", request.getAuthorizationExchange().getAuthorizationResponse().getCode());

        // client_secret이 설정된 경우에만 포함 — 미설정 시 전송하면 KOE010 발생
        String clientSecret = request.getClientRegistration().getClientSecret();
        if (StringUtils.hasText(clientSecret)) {
            params.add("client_secret", clientSecret);
        }

        Map<String, Object> body;
        try {
            body = restClient.post()
                    .uri(tokenUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(params)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientResponseException e) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_token_response"),
                    "카카오 토큰 요청 실패: " + e.getResponseBodyAsString(), e);
        }

        if (body == null || !body.containsKey("access_token")) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_token_response"), "액세스 토큰이 응답에 없습니다.");
        }

        OAuth2AccessTokenResponse.Builder builder = OAuth2AccessTokenResponse
                .withToken((String) body.get("access_token"))
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(body.containsKey("expires_in")
                        ? Long.parseLong(String.valueOf(body.get("expires_in")))
                        : 0L);

        if (body.containsKey("refresh_token")) {
            builder.refreshToken((String) body.get("refresh_token"));
        }

        return builder.build();
    }
}
