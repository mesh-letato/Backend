package com.pinmoa.core.global.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI coreOpenApi() {
		return new OpenAPI()
			.info(new Info()
				.title("PinMoa Core API")
				.version("v1")
				.description("User, space, place, review, notification APIs"));
	}
}
