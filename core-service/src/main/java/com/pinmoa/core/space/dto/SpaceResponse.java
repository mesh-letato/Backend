package com.pinmoa.core.space.dto;

import com.pinmoa.core.space.entity.Space;
import com.pinmoa.core.space.entity.SpaceType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SpaceResponse {

    private Long id;
    private Long ownerId;
    private String name;
    private String emoji;
    private SpaceType type;
    private String inviteCode;
    private LocalDateTime createdAt;

    public static SpaceResponse from(Space space) {
        return SpaceResponse.builder()
                .id(space.getId())
                .ownerId(space.getOwnerId())
                .name(space.getName())
                .emoji(space.getEmoji())
                .type(space.getType())
                .inviteCode(space.getInviteCode())
                .createdAt(space.getCreatedAt())
                .build();
    }
}
