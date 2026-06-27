package com.pinmoa.core.place.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record PlaceSaveRequest(
        @NotBlank String kakaoPlaceId,
        @NotBlank String name,
        String category,
        String address,
        @NotNull BigDecimal latitude,
        @NotNull BigDecimal longitude,
        String thumbnailUrl,
        @NotEmpty List<Long> spaceIds
) {
}
