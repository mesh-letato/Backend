# 핀모아 (PinMoa) ERD

```dbml
// ──────────────────────────────────────────
// 사용자
// ──────────────────────────────────────────
Table users {
  id            bigint      [pk, increment]
  email         varchar     [unique, null,  note: '이메일 로그인 식별자 (이메일 가입 시 사용)']
  password      varchar     [null,          note: 'BCrypt 해시 (이메일 가입 시에만 존재, OAuth 전용 계정은 null)']
  kakao_id      varchar     [unique, null,  note: '카카오 소셜 로그인 식별자']
  apple_id      varchar     [unique, null,  note: '애플 소셜 로그인 식별자']
  nickname      varchar     [not null]
  profile_image_url varchar  [null]
  created_at    timestamp   [not null, default: `now()`]
}

// ──────────────────────────────────────────
// 스페이스 (내 스페이스 + 공유 스페이스)
// ──────────────────────────────────────────
Table spaces {
  id          bigint    [pk, increment]
  owner_id    bigint    [not null, ref: > users.id]
  name        varchar   [not null]
  emoji       varchar   [null]
  type        varchar   [not null, note: 'MY | SHARED']
  invite_code varchar   [unique, null,  note: '공유 스페이스 초대 코드']
  created_at  timestamp [not null, default: `now()`]

  indexes {
    (owner_id, type) [note: '내 스페이스(MY) 해석 및 소유 스페이스 조회']
  }
}

// 스페이스 멤버 (다대다 조인)
Table space_members {
  id         bigint    [pk, increment]
  space_id   bigint    [not null, ref: > spaces.id]
  user_id    bigint    [not null, ref: > users.id]
  role       varchar   [not null, note: 'OWNER | MEMBER']
  joined_at  timestamp [not null, default: `now()`]

  indexes {
    (space_id, user_id) [unique]
  }
}

// ──────────────────────────────────────────
// 장소
// ──────────────────────────────────────────
Table places {
  id              bigint   [pk, increment]
  kakao_place_id  varchar  [unique, null, note: 'Kakao Map / Google Places 외부 ID']
  name            varchar  [not null]
  category        varchar  [null]
  address         varchar  [null]
  latitude        decimal  [not null]
  longitude       decimal  [not null]
  thumbnail_url   varchar  [null]
  created_at      timestamp [not null, default: `now()`]
}

// 스페이스-장소 저장 관계
Table saved_places {
  id         bigint    [pk, increment]
  space_id   bigint    [not null, ref: > spaces.id]
  place_id   bigint    [not null, ref: > places.id]
  saved_by   bigint    [not null, ref: > users.id]
  created_at timestamp [not null, default: `now()`]

  indexes {
    (space_id, place_id, saved_by) [unique]
  }
}

// ──────────────────────────────────────────
// SNS 링크 (AI 장소 추출 이력)
// ──────────────────────────────────────────
Table sns_links {
  id                 bigint    [pk, increment]
  user_id            bigint    [not null, ref: > users.id]
  url                varchar   [not null]
  platform           varchar   [not null, note: 'INSTAGRAM | TIKTOK | UNKNOWN']
  caption            text      [null]
  hashtags           text      [null, note: '쉼표 구분 또는 JSON 배열']
  extracted_place_id bigint    [null, ref: > places.id, note: 'AI가 추출한 최종 장소']
  created_at         timestamp [not null, default: `now()`]

  indexes {
    user_id [note: '유저별 링크 추출 이력 조회']
  }
}

// ──────────────────────────────────────────
// 후기 (폴라로이드 스토리, 24시간 TTL)
// ──────────────────────────────────────────
Table reviews {
  id         bigint    [pk, increment]
  user_id    bigint    [not null, ref: > users.id]
  space_id   bigint    [not null, ref: > spaces.id, note: '후기가 속한 스페이스']
  place_id   bigint    [not null, ref: > places.id]
  image_url  varchar   [not null]
  content    varchar   [not null, note: '한 줄 후기']
  created_at timestamp [not null, default: `now()`]

  indexes {
    (place_id, created_at) [note: '장소별 최신 후기 조회']
    (space_id, created_at) [note: '스페이스 로그 최신순 조회']
  }
}

// ──────────────────────────────────────────
// 알림
// ──────────────────────────────────────────
Table notifications {
  id                bigint    [pk, increment]
  user_id           bigint    [not null, ref: > users.id]
  type              varchar   [not null, note: 'PLACE_SAVED | REVIEW_UPLOADED | SPACE_JOINED | REVIEW_ON_MY_PLACE']
  message           varchar   [not null]
  is_read           boolean   [not null, default: false]
  related_space_id  bigint    [null, ref: > spaces.id]
  related_place_id  bigint    [null, ref: > places.id]
  created_at        timestamp [not null, default: `now()`]
}
```

---

## 관계 요약

| 관계 | 설명 |
|------|------|
| `users` 1 — N `spaces` | 한 유저가 여러 스페이스를 소유 |
| `spaces` N — N `users` | `space_members` 를 통해 다대다 |
| `spaces` N — N `places` | `saved_places` 를 통해 다대다 (누가 저장했는지 포함) |
| `users` 1 — N `sns_links` | 유저가 분석 요청한 SNS 링크 이력 |
| `sns_links` N — 1 `places` | AI 추출 결과로 연결된 장소 |
| `users` 1 — N `reviews` | 유저가 작성한 폴라로이드 후기 |
| `places` 1 — N `reviews` | 장소에 달린 후기 목록 |
| `spaces` 1 — N `reviews` | 스페이스 로그에 노출되는 후기 (선택적 연결) |
| `users` 1 — N `notifications` | 유저가 받는 알림 |
