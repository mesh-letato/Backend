package com.pinmoa.link.link.dto;

/**
 * 카카오맵 검색으로 찾은 장소 후보. 프론트엔드가 그대로 core-service 의 장소 저장 요청에 사용할 수 있도록
 * 저장에 필요한 필드를 모두 포함한다.
 */
public record PlaceCandidate(
	String kakaoPlaceId,
	String name,
	String category,
	String address,
	String roadAddress,
	Double latitude,
	Double longitude,
	String placeUrl,
	String phone
) {
}
