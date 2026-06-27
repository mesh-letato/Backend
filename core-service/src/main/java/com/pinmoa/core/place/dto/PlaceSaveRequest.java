package com.pinmoa.core.place.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PlaceSaveRequest(
    @NotBlank String kakaoPlaceId,
    @NotBlank String name,
    String category,
    String address,
    @NotNull BigDecimal latitude,
    @NotNull BigDecimal longitude,
    @NotNull Long spaceId
) {
}
