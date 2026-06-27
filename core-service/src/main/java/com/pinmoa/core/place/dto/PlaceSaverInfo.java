package com.pinmoa.core.place.dto;

import com.pinmoa.core.user.domain.User;

public record PlaceSaverInfo(
        Long userId,
        String nickname,
        String profileImageUrl
) {
    public static PlaceSaverInfo from(User user) {
        return new PlaceSaverInfo(user.getId(), user.getNickname(), user.getProfileImageUrl());
    }
}
