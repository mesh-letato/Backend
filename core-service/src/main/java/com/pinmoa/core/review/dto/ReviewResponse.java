package com.pinmoa.core.review.dto;

import com.pinmoa.core.review.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
	Long id,
	Long userId,
	Long spaceId,
	Long placeId,
	String imageUrl,
	String content,
	LocalDateTime expiresAt,
	LocalDateTime createdAt
) {
	public static ReviewResponse from(Review review) {
		return new ReviewResponse(
				review.getId(),
				review.getUserId(),
				review.getSpaceId(),
				review.getPlaceId(),
				review.getImageUrl(),
				review.getContent(),
				review.getExpiresAt(),
				review.getCreatedAt()
		);
	}
}
