package com.pinmoa.link.ai.dto;

/**
 * LLM 이 동영상 description 에서 추출한 장소 단서.
 * 카카오맵 키워드 검색 정확도를 높이기 위해 장소명과 지역 힌트를 분리해 받는다.
 */
public record ExtractedPlace(
	String name,
	String region
) {
	/**
	 * 카카오맵 검색에 사용할 키워드. 지역 힌트가 있으면 "지역 + 장소명" 형태로 합친다.
	 */
	public String toSearchKeyword() {
		if (region != null && !region.isBlank()) {
			return (region.trim() + " " + name.trim()).trim();
		}
		return name == null ? "" : name.trim();
	}
}
