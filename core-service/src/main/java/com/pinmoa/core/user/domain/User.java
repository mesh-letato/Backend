package com.pinmoa.core.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"social_type", "social_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(nullable = false)
    private String nickname;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "social_id")
    private String socialId;

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
    private User(String email, String password, String nickname, String profileImageUrl,
                  SocialType socialType, String socialId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.socialType = socialType;
        this.socialId = socialId;
    }

    public static User ofLocal(String email, String password, String nickname) {
        return User.builder()
            .email(email)
            .password(password)
            .nickname(nickname)
            .socialType(SocialType.LOCAL)
            .build();
    }

    public static User ofKakao(String email, String nickname, String profileImageUrl, String socialId) {
        return User.builder()
            .email(email)
            .nickname(nickname)
            .profileImageUrl(profileImageUrl)
            .socialType(SocialType.KAKAO)
            .socialId(socialId)
            .build();
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
