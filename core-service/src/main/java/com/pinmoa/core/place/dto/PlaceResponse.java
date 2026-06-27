package com.pinmoa.core.place.dto;

import com.pinmoa.core.place.domain.Place;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PlaceResponse(
        Long id,
        String kakaoPlaceId,
        String name,
        String category,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        String thumbnailUrl,
        LocalDateTime createdAt
) {
    public static PlaceResponse from(Place place) {
        return new PlaceResponse(
                place.getId(),
                place.getKakaoPlaceId(),
                place.getName(),
                place.getCategory(),
                place.getAddress(),
                place.getLatitude(),
                place.getLongitude(),
                place.getThumbnailUrl(),
                place.getCreatedAt()
        );
    }
}
