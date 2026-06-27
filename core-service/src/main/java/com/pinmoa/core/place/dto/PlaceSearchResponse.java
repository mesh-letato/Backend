package com.pinmoa.core.place.dto;

import java.math.BigDecimal;

public record PlaceSearchResponse(
    String kakaoPlaceId,
    String name,
    String category,
    String address,
    BigDecimal latitude,
    BigDecimal longitude
) {
    public static PlaceSearchResponse from(KakaoPlaceResult result) {
        return new PlaceSearchResponse(
            result.id(),
            result.placeName(),
            result.categoryName(),
            result.addressName(),
            result.latitude(),
            result.longitude()
        );
    }
}
