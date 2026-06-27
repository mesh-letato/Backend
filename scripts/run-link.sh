#!/usr/bin/env bash
# 로컬에서 link-service 를 .env 환경변수와 함께 실행하는 헬퍼 스크립트
set -a
# shellcheck disable=SC1091
source "$(dirname "$0")/../.env"
set +a
exec "$(dirname "$0")/../gradlew" :link-service:bootRun --console=plain
