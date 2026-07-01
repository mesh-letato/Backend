package com.pinmoa.link.link.dto;

/**
 * 인스타그램 임베드(captioned) 페이지에서 크롤러 UA로 가져온 캡션 추출 결과.
 */
public record InstaResult(
	String caption,
	boolean blocked,
	String error
) {
}