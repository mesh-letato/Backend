package com.pinmoa.core.place.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record KakaoPlaceResult(
    String id,
    @JsonProperty("place_name") String placeName,
    @JsonProperty("category_name") String categoryName,
    @JsonProperty("address_name") String addressName,
    String x,
    String y,
    @JsonProperty("place_url") String placeUrl
) {
    public BigDecimal latitude() {
        return new BigDecimal(y);
    }

    public BigDecimal longitude() {
        return new BigDecimal(x);
    }
}
