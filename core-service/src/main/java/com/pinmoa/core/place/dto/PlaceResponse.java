package com.pinmoa.core.place.dto;

import com.pinmoa.core.place.entity.Place;
import java.math.BigDecimal;

public record PlaceResponse(
    Long id,
    String kakaoPlaceId,
    String name,
    String category,
    String address,
    BigDecimal latitude,
    BigDecimal longitude
) {
    public static PlaceResponse from(Place place) {
        return new PlaceResponse(
            place.getId(),
            place.getKakaoPlaceId(),
            place.getName(),
            place.getCategory(),
            place.getAddress(),
            place.getLatitude(),
            place.getLongitude()
        );
    }
}
