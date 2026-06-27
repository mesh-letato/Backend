package com.pinmoa.link.ai.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExtractedPlaceTest {

	@DisplayName("지역 힌트가 있으면 '지역 + 장소명' 키워드를 만든다")
	@Test
	void keywordWithRegion() {
		ExtractedPlace place = new ExtractedPlace("미오 성수", "성수동");
		assertThat(place.toSearchKeyword()).isEqualTo("성수동 미오 성수");
	}

	@DisplayName("지역 힌트가 없으면 장소명만 사용한다")
	@Test
	void keywordWithoutRegion() {
		assertThat(new ExtractedPlace("센터커피", null).toSearchKeyword()).isEqualTo("센터커피");
		assertThat(new ExtractedPlace("센터커피", "").toSearchKeyword()).isEqualTo("센터커피");
		assertThat(new ExtractedPlace("센터커피", "   ").toSearchKeyword()).isEqualTo("센터커피");
	}
}
