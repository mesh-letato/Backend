package com.pinmoa.core.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/** 카카오 OAuth2 인증 후 userId를 성공 핸들러에 전달하기 위한 래퍼 */
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User delegate;
    private final Long userId;

    public CustomOAuth2User(OAuth2User delegate, Long userId) {
        this.delegate = delegate;
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return delegate.getAuthorities();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }
}
