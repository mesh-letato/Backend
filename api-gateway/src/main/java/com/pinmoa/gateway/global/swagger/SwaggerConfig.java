package com.pinmoa.gateway.global.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI gatewayOpenApi() {
		return new OpenAPI()
			.info(new Info()
				.title("PinMoa Gateway API")
				.version("v1")
				.description("PinMoa API Gateway entry points"));
	}
}
