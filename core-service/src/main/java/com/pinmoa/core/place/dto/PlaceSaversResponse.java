package com.pinmoa.core.place.dto;

import java.util.List;

public record PlaceSaversResponse(
        long savedCount,
        List<PlaceSaverInfo> savers
) {}
