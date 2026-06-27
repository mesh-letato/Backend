package com.pinmoa.link.ai.service;

import com.pinmoa.link.ai.dto.ExtractedPlace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseOutput;
import software.amazon.awssdk.services.bedrockruntime.model.Message;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class BedrockPlaceTextExtractorTest {

	@Mock
	private BedrockRuntimeClient bedrockRuntimeClient;

	private BedrockPlaceTextExtractor extractor;

	@BeforeEach
	void setUp() {
		extractor = new BedrockPlaceTextExtractor(bedrockRuntimeClient);
		ReflectionTestUtils.setField(extractor, "modelId", "test-model");
	}

	private void stubModelOutput(String text) {
		ConverseResponse response = ConverseResponse.builder()
			.output(ConverseOutput.fromMessage(Message.builder()
				.role(ConversationRole.ASSISTANT)
				.content(ContentBlock.fromText(text))
				.build()))
			.build();
		when(bedrockRuntimeClient.converse(any(ConverseRequest.class))).thenReturn(response);
	}

	@DisplayName("LLM이 반환한 JSON 배열을 장소 목록으로 파싱한다")
	@Test
	void parsesJsonArray() {
		stubModelOutput("[{\"name\":\"미오 성수\",\"region\":\"성수동\"},"
			+ "{\"name\":\"센터커피\",\"region\":\"성수\"}]");

		List<ExtractedPlace> places = extractor.extract("성수동 맛집 설명글");

		assertThat(places).hasSize(2);
		assertThat(places.get(0).name()).isEqualTo("미오 성수");
		assertThat(places.get(0).region()).isEqualTo("성수동");
	}

	@DisplayName("모델이 부가 텍스트를 덧붙여도 JSON 배열만 추출해 파싱한다")
	@Test
	void parsesJsonWrappedInProse() {
		stubModelOutput("다음은 찾은 장소입니다:\n[{\"name\":\"미오 성수\",\"region\":\"성수동\"}]\n이상입니다.");

		List<ExtractedPlace> places = extractor.extract("설명글");

		assertThat(places).hasSize(1);
		assertThat(places.get(0).name()).isEqualTo("미오 성수");
	}

	@DisplayName("JSON 배열이 없는 응답은 빈 목록을 반환한다")
	@Test
	void returnsEmptyWhenNoArray() {
		stubModelOutput("장소를 찾지 못했습니다.");

		assertThat(extractor.extract("설명글")).isEmpty();
	}

	@DisplayName("입력 텍스트가 비어 있으면 모델을 호출하지 않고 빈 목록을 반환한다")
	@Test
	void skipsModelForBlankInput() {
		assertThat(extractor.extract("  ")).isEmpty();
		assertThat(extractor.extract(null)).isEmpty();
		verifyNoInteractions(bedrockRuntimeClient);
	}
}
