package com.pinmoa.link.link.dto;

import java.util.List;

/**
 * 링크 추출 최종 응답. 감지된 플랫폼과 카카오맵에서 찾은 장소 후보 목록을 반환한다.
 */
public record LinkExtractResponse(
	String platform,
	List<PlaceCandidate> candidates
) {
}
