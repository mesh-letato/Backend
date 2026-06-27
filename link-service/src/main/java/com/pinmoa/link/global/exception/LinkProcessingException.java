package com.pinmoa.link.global.exception;

/**
 * 링크 처리 파이프라인(메타데이터 추출, LLM 호출, 장소 검색) 중 발생한 오류.
 */
public class LinkProcessingException extends RuntimeException {

	public LinkProcessingException(String message) {
		super(message);
	}

	public LinkProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
}
