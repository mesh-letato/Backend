package com.pinmoa.core.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequest(
	@NotNull(message = "placeId는 필수입니다.")
	Long placeId,

	// 후기가 노출될 스페이스 (선택)
	Long spaceId,

	@NotBlank(message = "imageUrl은 필수입니다.")
	String imageUrl,

	@NotBlank(message = "content는 필수입니다.")
	String content
) {
}
