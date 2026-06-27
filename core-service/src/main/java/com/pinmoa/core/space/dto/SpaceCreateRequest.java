package com.pinmoa.core.space.dto;

import com.pinmoa.core.space.entity.SpaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SpaceCreateRequest(
        @NotBlank(message = "스페이스 이름은 필수입니다.") String name,
        String emoji,
        @NotNull(message = "스페이스 타입은 필수입니다.") SpaceType type
) {
}
