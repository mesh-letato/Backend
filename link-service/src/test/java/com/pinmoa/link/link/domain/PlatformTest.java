package com.pinmoa.link.link.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlatformTest {

	@DisplayName("URL로부터 플랫폼을 판별한다")
	@ParameterizedTest
	@CsvSource({
		"https://www.instagram.com/reel/abc123/, INSTAGRAM",
		"https://instagram.com/p/xyz, INSTAGRAM",
		"https://www.tiktok.com/@user/video/123, TIKTOK",
		"https://youtube.com/watch?v=abc, YOUTUBE",
		"https://youtu.be/abc, YOUTUBE",
		"https://example.com/something, UNKNOWN"
	})
	void detect(String url, Platform expected) {
		assertThat(Platform.detect(url)).isEqualTo(expected);
	}

	@DisplayName("URL이 null이면 UNKNOWN을 반환한다")
	@Test
	void detectNull() {
		assertThat(Platform.detect(null)).isEqualTo(Platform.UNKNOWN);
	}

	@DisplayName("대소문자가 섞여도 판별한다")
	@Test
	void detectCaseInsensitive() {
		assertThat(Platform.detect("https://www.INSTAGRAM.com/reel/abc")).isEqualTo(Platform.INSTAGRAM);
	}
}
