# PinMoa Backend

PinMoa backend is organized as a small MSA skeleton with three applications:

- `api-gateway`: external entry point and route forwarding
- `core-service`: user, space, place, review, notification domains
- `link-service`: SNS link parsing and AI extraction domain

Each bounded context is separated into `controller`, `service`, `repository`, and `dto` packages.

## Dev DB

Run the local PostgreSQL container:

```bash
docker compose -f docker-compose.dev.yml up -d
```

Core service local config:

1. Copy `core-service/src/main/resources/application-local.example.yml`
2. Create `core-service/src/main/resources/application-local.yml`
3. Run the service with `local` profile

Example:

```bash
SPRING_PROFILES_ACTIVE=local ./gradlew :core-service:bootRun
```
