package com.pinmoa.core.auth.service;

import com.pinmoa.core.auth.dto.CustomOAuth2User;
import com.pinmoa.core.user.domain.User;
import com.pinmoa.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String kakaoId = String.valueOf(attributes.get("id"));

        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) {
            throw new OAuth2AuthenticationException(
                    new org.springframework.security.oauth2.core.OAuth2Error("kakao_account_missing"),
                    "kakao_account 정보가 없습니다. 카카오 앱의 동의항목을 확인하세요.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) {
            throw new OAuth2AuthenticationException(
                    new org.springframework.security.oauth2.core.OAuth2Error("profile_missing"),
                    "profile 정보가 없습니다. profile_nickname 동의항목을 확인하세요.");
        }

        String nickname = (String) profile.get("nickname");
        // TODO: 카카오 앱 동의항목에 profile_image 추가 후 아래 코드로 교체
        // String profileImageUrl = (String) profile.get("profile_image_url");

        // 기존 회원 조회 → 없으면 자동 가입
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> userRepository.save(User.ofKakao(kakaoId, nickname, null)));

        return new CustomOAuth2User(oAuth2User, user.getId());
    }
}
