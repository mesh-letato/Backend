# PinMoa Backend

PinMoa backend is organized as a small MSA skeleton with three applications:

- `api-gateway`: external entry point and route forwarding
- `core-service`: user, space, place, review, notification domains
- `link-service`: SNS link parsing and AI extraction domain

Each bounded context is separated into `controller`, `service`, `repository`, and `dto` packages.
