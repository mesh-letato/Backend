package com.pinmoa.core.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "입력값이 올바르지 않습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "INVALID_PASSWORD", "비밀번호가 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_REFRESH_TOKEN", "만료된 리프레시 토큰입니다."),
    KAKAO_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "KAKAO_AUTH_FAILED", "카카오 인증에 실패했습니다."),

    // Space
    SPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "SPACE_NOT_FOUND", "스페이스를 찾을 수 없습니다."),
    INVALID_INVITE_CODE(HttpStatus.NOT_FOUND, "INVALID_INVITE_CODE", "유효하지 않은 초대 코드입니다."),
    ALREADY_SPACE_MEMBER(HttpStatus.CONFLICT, "ALREADY_SPACE_MEMBER", "이미 참여 중인 스페이스입니다."),
    UNAUTHORIZED(HttpStatus.FORBIDDEN, "UNAUTHORIZED", "권한이 없습니다."),

    // Place
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "PLACE_NOT_FOUND", "장소를 찾을 수 없습니다."),
    PLACE_ALREADY_SAVED(HttpStatus.CONFLICT, "PLACE_ALREADY_SAVED", "이미 저장된 장소입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
