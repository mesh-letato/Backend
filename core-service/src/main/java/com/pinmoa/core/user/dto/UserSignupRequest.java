package com.pinmoa.core.user.dto;

public record UserSignupRequest(
	String socialType,
	String socialId,
	String nickname
) {
}
