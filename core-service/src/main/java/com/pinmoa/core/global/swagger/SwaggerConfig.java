package com.pinmoa.core.global.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                .description("User, space, place, review, notification APIs"))
            .addSecurityItem(new SecurityRequirement().addList("X-User-Id"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("X-User-Id", new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)
                    .name("X-User-Id")
                    .description("게이트웨이가 주입하는 사용자 ID (직접 테스트 시 입력)")));
    }
}
