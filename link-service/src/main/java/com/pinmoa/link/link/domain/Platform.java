package com.pinmoa.link.link.domain;

/**
 * SNS 링크의 플랫폼 종류. URL로부터 판별한다.
 */
public enum Platform {
	INSTAGRAM,
	TIKTOK,
	YOUTUBE,
	UNKNOWN;

	public static Platform detect(String url) {
		if (url == null) {
			return UNKNOWN;
		}
		String u = url.toLowerCase();
		if (u.contains("instagram.com")) {
			return INSTAGRAM;
		}
		if (u.contains("tiktok.com")) {
			return TIKTOK;
		}
		if (u.contains("youtube.com") || u.contains("youtu.be")) {
			return YOUTUBE;
		}
		return UNKNOWN;
	}
}
