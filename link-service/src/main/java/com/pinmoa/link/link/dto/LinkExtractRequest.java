package com.pinmoa.link.link.dto;

import jakarta.validation.constraints.NotBlank;

public record LinkExtractRequest(
	@NotBlank(message = "url은 필수입니다.")
	String url
) {
}
