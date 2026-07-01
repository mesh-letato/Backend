package com.pinmoa.core.user.dto;

public record KakaoUserInfo(
    String socialId,
    String email,
    String nickname,
    String profileImageUrl
) {
}
