package com.pinmoa.core.review.dto;

public record ReviewCreateRequest(
	Long placeId,
	String imageUrl,
	String content
) {
}
