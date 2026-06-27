package com.pinmoa.core.user.service;

import com.pinmoa.core.global.exception.BusinessException;
import com.pinmoa.core.global.exception.ErrorCode;
import com.pinmoa.core.space.dto.SpaceCreateRequest;
import com.pinmoa.core.space.entity.SpaceType;
import com.pinmoa.core.space.service.SpaceService;
import com.pinmoa.core.user.domain.User;
import com.pinmoa.core.user.dto.*;
import com.pinmoa.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SpaceService spaceService;

    @Transactional
    public UserResponse signup(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .nickname(request.nickname())
            .build();
        userRepository.save(user);

        // 가입 완료 시 내 스페이스 자동 생성 (기획서 F-03)
        spaceService.createSpace(user.getId(), new SpaceCreateRequest("내 스페이스", null, SpaceType.MY));

        return UserResponse.from(user);
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
        userRepository.delete(findById(userId));
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
