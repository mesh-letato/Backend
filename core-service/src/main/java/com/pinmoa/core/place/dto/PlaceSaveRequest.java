package com.pinmoa.core.place.dto;

import java.util.List;

public record PlaceSaveRequest(
	Long placeId,
	List<Long> spaceIds
) {
}
