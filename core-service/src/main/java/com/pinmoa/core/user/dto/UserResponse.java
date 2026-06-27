package com.pinmoa.core.user.dto;

import com.pinmoa.core.user.domain.User;

public record UserResponse(
    Long id,
    String email,
    String nickname,
    String profileImageUrl
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getProfileImageUrl()
        );
    }
}
