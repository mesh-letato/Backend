# 공통 응답 구조

## 개요

성공 응답은 각 도메인 DTO를 그대로 반환하고, 실패 응답은 공통 `ErrorResponse` 구조를 사용합니다.

## 응답 구조

### 성공 (2xx)

별도 wrapper 없이 도메인 객체를 그대로 반환합니다.

```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "홍길동"
}
```

### 실패 (4xx / 5xx)

```json
{
  "code": "USER_NOT_FOUND",
  "message": "사용자를 찾을 수 없습니다."
}
```

---

## 추가된 파일

| 파일 | 역할 |
|------|------|
| `global/exception/ErrorCode.java` | 에러 코드 enum (HTTP 상태코드 + code + message) |
| `global/exception/BusinessException.java` | 커스텀 예외 클래스 |
| `global/exception/ErrorResponse.java` | 에러 응답 record |
| `global/exception/GlobalExceptionHandler.java` | 전역 예외 처리 (`@RestControllerAdvice`) |

---

## 사용법

### 1. 예외 던지기

```java
throw new BusinessException(ErrorCode.USER_NOT_FOUND);
```

### 2. 새 에러 코드 추가

`ErrorCode.java`에만 추가하면 됩니다.

```java
SPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "SPACE_NOT_FOUND", "스페이스를 찾을 수 없습니다."),
```

---

## 현재 정의된 ErrorCode

| Code | HTTP | 설명 |
|------|------|------|
| `INVALID_INPUT` | 400 | 입력값 오류 (Validation 실패 시 자동 사용) |
| `RESOURCE_NOT_FOUND` | 404 | 리소스 없음 (범용) |
| `INTERNAL_SERVER_ERROR` | 500 | 서버 오류 (미처리 예외 시 자동 사용) |
| `USER_NOT_FOUND` | 404 | 사용자 없음 |
| `EMAIL_ALREADY_EXISTS` | 409 | 이메일 중복 |

---

## 예외 처리 흐름

```
Controller/Service
    └─ throw new BusinessException(ErrorCode.XXX)
            └─ GlobalExceptionHandler.handleBusiness()
                    └─ ResponseEntity<ErrorResponse> 반환
```

`@Valid` 실패 → `handleValidation()` 자동 처리  
미처리 예외 → `handleException()` → 500 반환