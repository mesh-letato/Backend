package com.pinmoa.link.global.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI linkOpenApi() {
		return new OpenAPI()
			.info(new Info()
				.title("PinMoa Link API")
				.version("v1")
				.description("SNS link parsing and AI extraction APIs"));
	}
}
