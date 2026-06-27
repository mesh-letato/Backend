# 핀모아 (PinMoa) — CLAUDE.md

## 프로젝트 개요

SNS 링크에서 장소를 AI로 추출하고, 스페이스(공유 지도)에 저장·공유하는 모바일 서비스의 백엔드.

- **core-service**: 사용자, 스페이스, 장소, 후기, 알림 도메인
- **link-service**: SNS 링크 파싱 및 AI 장소 추출
- **api-gateway**: 라우팅

---

## 기술 스택

- Java 17, Spring Boot, Spring Data JPA
- PostgreSQL (dev: Docker로 실행)
- Gradle 멀티 모듈

---

## 로컬 개발 환경 세팅

### 1. DB 실행

```bash
docker-compose -f docker-compose.dev.yml up -d
```

PostgreSQL이 `localhost:5432`에 뜹니다. **로컬에 PostgreSQL이 설치되어 있으면 포트 충돌이 발생하므로 반드시 중지 후 실행하세요.**

```bash
brew services stop postgresql@14  # 버전에 맞게
```

### 2. application-local.yml 생성

`core-service/src/main/resources/application-local.example.yml`을 복사해 `application-local.yml`로 만드세요.

```bash
cp core-service/src/main/resources/application-local.example.yml \
   core-service/src/main/resources/application-local.yml
```

### 3. 실행

```bash
./gradlew :core-service:bootRun
```

---

## 패키지 구조 (core-service)

```
com.pinmoa.core
├── global
│   └── exception
│       ├── ErrorCode.java          # 에러 코드 enum
│       ├── BusinessException.java  # 커스텀 예외
│       ├── ErrorResponse.java      # 에러 응답 DTO
│       └── GlobalExceptionHandler.java
├── user/
├── space/
├── place/
├── review/
└── notification/
```

각 도메인은 `controller / service / repository / domain / dto` 구조를 따릅니다.

---

## 공통 응답 구조

- **성공**: 도메인 객체를 그대로 반환 (wrapper 없음)
- **실패**: `ErrorResponse { code, message }` 반환

### 예외 던지는 법

```java
throw new BusinessException(ErrorCode.USER_NOT_FOUND);
```

### 새 에러 코드 추가

`ErrorCode.java`에만 추가하면 됩니다.

```java
SPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "SPACE_NOT_FOUND", "스페이스를 찾을 수 없습니다."),
```

자세한 내용: `docs/common-response-structure.md`

---

## 브랜치 전략

- `main`: 배포 브랜치
- `develop`: 개발 기본 브랜치
- `feat/기능이름`: 기능 개발
- `refactor/이름`: 버그 수정 및 리팩토링

모든 작업은 `develop`에서 분기하고 `develop`으로 머지합니다.

자세한 내용: `docs/git-branch-strategy.md`

---

## 주요 문서

| 문서 | 경로 |
|------|------|
| 기획서 | `docs/기획서.md` |
| ERD | `docs/erd.md` |
| 공통 응답 구조 | `docs/common-response-structure.md` |
| 브랜치 전략 | `docs/git-branch-strategy.md` |