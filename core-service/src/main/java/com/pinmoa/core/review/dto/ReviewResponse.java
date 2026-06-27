package com.pinmoa.core.review.dto;

import com.pinmoa.core.review.entity.Review;
import com.pinmoa.core.user.domain.User;

import java.time.LocalDateTime;

public record ReviewResponse(
    Long id,
    Long userId,
    String nickname,
    String profileImageUrl,
    Long spaceId,
    Long placeId,
    String imageUrl,
    String content,
    LocalDateTime expiresAt,
    LocalDateTime createdAt
) {
    public static ReviewResponse of(Review review, User user) {
        return new ReviewResponse(
                review.getId(),
                review.getUserId(),
                user != null ? user.getNickname() : null,
                user != null ? user.getProfileImageUrl() : null,
                review.getSpaceId(),
                review.getPlaceId(),
                review.getImageUrl(),
                review.getContent(),
                review.getExpiresAt(),
                review.getCreatedAt()
        );
    }
}
