package com.pinmoa.link.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinmoa.link.ai.dto.ExtractedPlace;
import com.pinmoa.link.global.exception.LinkProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.InferenceConfiguration;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import software.amazon.awssdk.services.bedrockruntime.model.SystemContentBlock;

import java.util.List;

/**
 * AWS Bedrock(Converse API)을 사용해 텍스트에서 장소 단서를 추출한다.
 * 모델에 JSON 배열만 반환하도록 지시하고, 응답을 파싱한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BedrockPlaceTextExtractor implements PlaceTextExtractor {

	private static final String SYSTEM_PROMPT = """
		너는 짧은 동영상(릴스/틱톡/쇼츠)의 설명글에서 '실제 방문 가능한 장소'를 찾아내는 도우미다.
		입력으로 동영상 설명글이 주어진다. 설명글에 등장하는 음식점, 카페, 명소 등 장소를 찾아라.
		반드시 아래 형식의 JSON 배열만 출력한다. 다른 설명, 마크다운, 코드블록을 절대 포함하지 마라.
		[{"name": "장소 이름", "region": "지역(동/구/시) 또는 빈 문자열"}]
		장소를 찾을 수 없으면 빈 배열 []을 출력한다.
		장소 이름은 검색 가능한 고유명사로, 지역은 '성수동', '서울 강남' 처럼 검색에 도움이 되는 형태로 적는다.
		""";

	private final BedrockRuntimeClient bedrockRuntimeClient;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${aws.bedrock.model-id}")
	private String modelId;

	@Override
	public List<ExtractedPlace> extract(String text) {
		if (text == null || text.isBlank()) {
			return List.of();
		}

		String output = invokeModel(text);
		return parsePlaces(output);
	}

	private String invokeModel(String text) {
		try {
			Message userMessage = Message.builder()
				.role(ConversationRole.USER)
				.content(ContentBlock.fromText(text))
				.build();

			ConverseRequest request = ConverseRequest.builder()
				.modelId(modelId)
				.system(SystemContentBlock.fromText(SYSTEM_PROMPT))
				.messages(userMessage)
				.inferenceConfig(InferenceConfiguration.builder()
					.maxTokens(1024)
					.temperature(0.0f)
					.build())
				.build();

			ConverseResponse response = bedrockRuntimeClient.converse(request);
			return response.output().message().content().get(0).text();
		} catch (RuntimeException e) {
			log.error("Bedrock 장소 추출 호출 실패", e);
			throw new LinkProcessingException("LLM 장소 추출에 실패했습니다.", e);
		}
	}

	/**
	 * 모델 출력에서 JSON 배열 부분만 잘라 파싱한다. 모델이 부가 텍스트를 덧붙여도 견딜 수 있게 방어적으로 처리한다.
	 */
	private List<ExtractedPlace> parsePlaces(String output) {
		if (output == null || output.isBlank()) {
			return List.of();
		}
		int start = output.indexOf('[');
		int end = output.lastIndexOf(']');
		if (start < 0 || end < 0 || end <= start) {
			log.warn("LLM 응답에서 JSON 배열을 찾지 못했습니다: {}", output);
			return List.of();
		}
		String json = output.substring(start, end + 1);
		try {
			ExtractedPlace[] places = objectMapper.readValue(json, ExtractedPlace[].class);
			return List.of(places);
		} catch (Exception e) {
			log.warn("LLM 응답 JSON 파싱 실패: {}", json, e);
			return List.of();
		}
	}
}
