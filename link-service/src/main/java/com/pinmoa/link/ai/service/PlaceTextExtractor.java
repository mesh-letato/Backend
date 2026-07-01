package com.pinmoa.link.ai.service;

import com.pinmoa.link.ai.dto.ExtractedPlace;

import java.util.List;

/**
 * 텍스트(동영상 description 등)에서 장소 단서를 추출하는 컴포넌트.
 * 구현체는 LLM(현재 Google Gemini)을 사용하며, 인터페이스로 분리해 다른 모델로 교체 가능하게 한다.
 */
public interface PlaceTextExtractor {

	List<ExtractedPlace> extract(String text);
}
