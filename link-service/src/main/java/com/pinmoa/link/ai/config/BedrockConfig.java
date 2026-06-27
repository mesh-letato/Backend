package com.pinmoa.link.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

@Configuration
public class BedrockConfig {

	@Value("${aws.region}")
	private String region;

	/**
	 * 인증은 환경변수 AWS_BEARER_TOKEN_BEDROCK(Bedrock API 키)를 SDK가 자동 감지해 사용한다.
	 * 베어러 토큰이 없으면 SDK 기본 자격 증명 체인(SigV4)으로 폴백한다.
	 * 따라서 credentialsProvider 를 명시적으로 지정하지 않는다.
	 */
	@Bean
	public BedrockRuntimeClient bedrockRuntimeClient() {
		return BedrockRuntimeClient.builder()
			.region(Region.of(region))
			.build();
	}
}
