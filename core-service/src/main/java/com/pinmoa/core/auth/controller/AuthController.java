package com.pinmoa.core.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Tag(name = "Auth", description = "소셜 로그인 관련 API")
@RestController
@RequestMapping("/api/core/auth")
public class AuthController {

    @Operation(summary = "카카오 로그인", description = "카카오 OAuth2 로그인 페이지로 리다이렉트합니다.")
    @GetMapping("/kakao")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/kakao");
    }
}
