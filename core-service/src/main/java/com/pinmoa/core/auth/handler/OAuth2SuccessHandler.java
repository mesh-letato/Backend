package com.pinmoa.core.auth.handler;

import com.pinmoa.core.auth.dto.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String FRONTEND_BASE_URL = "http://localhost:5173";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = oAuth2User.getUserId();

        // TODO: JWT 완성 후 userId 쿼리 파라미터 → Authorization 헤더 토큰으로 교체
        String redirectUrl = FRONTEND_BASE_URL + "?userId=" + userId;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
