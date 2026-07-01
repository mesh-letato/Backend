package com.pinmoa.core.user.dto;

/**
 * 둘 중 하나를 채워서 보낸다.
 * - kakaoAccessToken: 네이티브 앱(카카오 SDK)이 이미 발급받은 access token을 바로 전달하는 경우
 * - code + redirectUri: 웹(SPA)이 인가 코드 플로우로 받은 code를 전달하는 경우. 백엔드가 access token으로 직접 교환한다
 *   (client_secret이 활성화된 카카오 앱이어도 브라우저에 시크릿을 노출하지 않기 위해 교환은 항상 서버에서 수행).
 */
public record KakaoLoginRequest(
    String kakaoAccessToken,
    String code,
    String redirectUri
) {
}
