package com.pinmoa.link.ai.dto;

public record AiExtractCandidateResponse(
	String name,
	String category,
	String address,
	Double score
) {
}
