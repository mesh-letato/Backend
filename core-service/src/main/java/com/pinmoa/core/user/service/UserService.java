package com.pinmoa.core.user.service;

import com.pinmoa.core.global.exception.BusinessException;
import com.pinmoa.core.global.exception.ErrorCode;
import com.pinmoa.core.user.domain.User;
import com.pinmoa.core.user.dto.*;
import com.pinmoa.core.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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
        return UserResponse.from(userRepository.save(user));
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

    @Transactional(readOnly = true)
    public List<UserResponse> searchByNickname(String query) {
        String nickname = query.startsWith("@") ? query.substring(1) : query;
        return userRepository.findByNicknameContaining(nickname)
            .stream()
            .map(UserResponse::from)
            .toList();
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
