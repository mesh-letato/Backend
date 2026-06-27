package com.pinmoa.core.user.dto;

public record UserLoginResponse(
    Long userId,
    String email,
    String nickname,
    String accessToken,
    String refreshToken
) {
}
