package com.pinmoa.core.user.service;

import com.pinmoa.core.global.exception.BusinessException;
import com.pinmoa.core.global.exception.ErrorCode;
import com.pinmoa.core.global.jwt.JwtUtil;
import com.pinmoa.core.user.client.KakaoUserClient;
import com.pinmoa.core.user.domain.RefreshToken;
import com.pinmoa.core.user.domain.SocialType;
import com.pinmoa.core.user.domain.User;
import com.pinmoa.core.user.dto.*;
import com.pinmoa.core.user.repository.RefreshTokenRepository;
import com.pinmoa.core.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final KakaoUserClient kakaoUserClient;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Transactional
    public UserResponse signup(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = User.ofLocal(request.email(), passwordEncoder.encode(request.password()), request.nickname());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        return issueTokens(user);
    }

    @Transactional
    public UserLoginResponse kakaoLogin(KakaoLoginRequest request) {
        String accessToken = resolveKakaoAccessToken(request);
        KakaoUserInfo kakaoUserInfo = kakaoUserClient.getUserInfo(accessToken);

        User user = userRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, kakaoUserInfo.socialId())
            .orElseGet(() -> createKakaoUser(kakaoUserInfo));

        return issueTokens(user);
    }

    private String resolveKakaoAccessToken(KakaoLoginRequest request) {
        if (request.kakaoAccessToken() != null && !request.kakaoAccessToken().isBlank()) {
            return request.kakaoAccessToken();
        }
        if (request.code() != null && !request.code().isBlank()
            && request.redirectUri() != null && !request.redirectUri().isBlank()) {
            return kakaoUserClient.exchangeCodeForAccessToken(request.code(), request.redirectUri());
        }
        throw new BusinessException(ErrorCode.INVALID_INPUT);
    }

    private User createKakaoUser(KakaoUserInfo kakaoUserInfo) {
        if (userRepository.existsByEmail(kakaoUserInfo.email())) {
            // ponytail: 로컬 계정과 카카오 계정 자동 연동은 범위 밖. 계정 연동이 필요해지면 여기서 처리.
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        return userRepository.save(User.ofKakao(
            kakaoUserInfo.email(),
            kakaoUserInfo.nickname(),
            kakaoUserInfo.profileImageUrl(),
            kakaoUserInfo.socialId()
        ));
    }

    private UserLoginResponse issueTokens(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getNickname());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshExpiration / 1000);
        refreshTokenRepository.findByUserId(user.getId())
            .ifPresentOrElse(
                rt -> rt.update(refreshToken, expiresAt),
                () -> refreshTokenRepository.save(RefreshToken.builder()
                    .userId(user.getId())
                    .token(refreshToken)
                    .expiresAt(expiresAt)
                    .build())
            );

        return new UserLoginResponse(user.getId(), user.getEmail(), user.getNickname(), accessToken, refreshToken);
    }

    @Transactional
    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        Long userId = jwtUtil.getUserId(request.refreshToken());
        User user = findById(userId);
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getNickname());

        return new TokenRefreshResponse(newAccessToken);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {
        return UserResponse.from(findById(userId));
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = findById(userId);
        user.updateProfile(request.nickname(), request.profileImageUrl());
        return UserResponse.from(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        userRepository.delete(findById(userId));
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
