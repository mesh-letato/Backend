package com.pinmoa.core.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    private String profileImageUrl;

    @Column(name = "kakao_id", unique = true)
    private String kakaoId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    private User(String email, String password, String nickname, String profileImageUrl) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    /** 카카오 소셜 로그인으로 신규 가입하는 사용자 생성 */
    public static User ofKakao(String kakaoId, String nickname, String profileImageUrl) {
        User user = User.builder()
                .email("kakao_" + kakaoId + "@pinmoa.com")
                .password("KAKAO_OAUTH")
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();
        user.kakaoId = kakaoId;
        return user;
    }
}
