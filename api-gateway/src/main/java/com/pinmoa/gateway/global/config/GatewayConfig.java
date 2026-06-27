package com.pinmoa.gateway.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayConfig {

	@Value("${CORE_SERVICE_URL:http://localhost:8081}")
	private String coreServiceUrl;

	@Value("${LINK_SERVICE_URL:http://localhost:8082}")
	private String linkServiceUrl;

	@Bean
	RouterFunction<ServerResponse> coreServiceRoute() {
		return GatewayRouterFunctions.route("core-service")
			.route(path("/api/core/**"), HandlerFunctions.http(coreServiceUrl))
			.build();
	}

	@Bean
	RouterFunction<ServerResponse> linkServiceRoute() {
		return GatewayRouterFunctions.route("link-service")
			.route(path("/api/link/**"), HandlerFunctions.http(linkServiceUrl))
			.build();
	}
}
