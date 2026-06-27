package com.pinmoa.core.global.swagger;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI coreOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("PinMoa Core API")
                .version("v1")
                .description("User, space, place, review, notification APIs"))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
