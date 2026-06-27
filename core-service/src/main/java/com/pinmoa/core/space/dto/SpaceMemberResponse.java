package com.pinmoa.core.space.dto;

import com.pinmoa.core.space.entity.SpaceMember;
import com.pinmoa.core.space.entity.SpaceRole;
import com.pinmoa.core.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpaceMemberResponse {

    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private SpaceRole role;

    public static SpaceMemberResponse of(SpaceMember member, User user) {
        return SpaceMemberResponse.builder()
                .userId(member.getUserId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .role(member.getRole())
                .build();
    }
}