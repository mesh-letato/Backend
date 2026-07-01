package com.pinmoa.core.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final String FRONTEND_BASE_URL = "http://localhost:5173";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        String errorCode = "unknown";
        if (exception instanceof OAuth2AuthenticationException oauthEx) {
            errorCode = oauthEx.getError().getErrorCode();
        }
        log.error("[OAuth2 로그인 실패] errorCode={}, message={}", errorCode, exception.getMessage(), exception);

        String redirectUrl = FRONTEND_BASE_URL + "?error=" + URLEncoder.encode(errorCode, StandardCharsets.UTF_8)
                + "&message=" + URLEncoder.encode(exception.getMessage() != null ? exception.getMessage() : "", StandardCharsets.UTF_8);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
