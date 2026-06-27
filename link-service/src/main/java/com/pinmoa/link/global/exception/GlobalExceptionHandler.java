package com.pinmoa.link.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
			.findFirst()
			.map(error -> error.getDefaultMessage())
			.orElse("입력값이 올바르지 않습니다.");
		return ResponseEntity.badRequest()
			.body(Map.of("code", "INVALID_INPUT", "message", message));
	}

	@ExceptionHandler(LinkProcessingException.class)
	public ResponseEntity<Map<String, String>> handleLinkProcessing(LinkProcessingException e) {
		log.error("링크 처리 실패", e);
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
			.body(Map.of("code", "LINK_PROCESSING_FAILED", "message", e.getMessage()));
	}
}
