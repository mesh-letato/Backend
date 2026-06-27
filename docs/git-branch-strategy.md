# Git Branch Strategy

## Branch Roles

- `main`
  - release branch
  - 배포 가능한 안정 버전만 반영합니다.

- `develop`
  - development branch
  - 기능 개발과 수정 작업을 먼저 모으는 기본 브랜치입니다.

## Working Rule

- 모든 작업 브랜치는 `develop`에서 분기합니다.
- 작업이 끝나면 `develop`으로 merge 합니다.
- release 시점에 `develop`에서 `main`으로 반영합니다.

## Branch Naming

### 1. Feature Branch

- 새로운 기능 개발은 `feat/기능이름` 형식으로 생성합니다.
- 예시
  - `feat/login`
  - `feat/space-create`
  - `feat/link-extract`

### 2. Bug Fix Branch

- 버그 수정 또는 코드 수정 작업은 `refactor/버그이름` 형식으로 생성합니다.
- 예시
  - `refactor/login-error`
  - `refactor/space-invite-bug`
  - `refactor/link-parse-fail`

## Recommended Flow

1. `develop` 최신 상태를 기준으로 작업 브랜치를 생성합니다.
2. 작업 내용에 맞게 `feat/...` 또는 `refactor/...` 브랜치를 사용합니다.
3. 개발 완료 후 `develop`으로 merge 합니다.
4. 배포 시 `develop` 내용을 `main`에 반영합니다.

## Summary

- `main` = release branch
- `develop` = development branch
- 기능 개발 = `feat/기능이름`
- 버그 수정 = `refactor/버그이름`
