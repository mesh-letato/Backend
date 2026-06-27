package com.pinmoa.core.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SPACE_NOT_FOUND("스페이스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("권한이 없습니다.", HttpStatus.FORBIDDEN),
    INVALID_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}