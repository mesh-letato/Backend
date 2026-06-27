package com.pinmoa.gateway.global.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthFilterTest {

	private static final String SECRET = "pinmoa-jwt-secret-key-must-be-at-least-32-characters-long";

	private JwtAuthFilter filter;

	@BeforeEach
	void setUp() {
		filter = new JwtAuthFilter(new GatewayJwtUtil(SECRET));
	}

	private String token(long userId) {
		SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
		return Jwts.builder().subject(String.valueOf(userId)).signWith(key).compact();
	}

	@DisplayName("유효한 토큰이면 검증된 X-User-Id를 주입해 downstream으로 통과시킨다")
	@Test
	void validTokenInjectsUserId() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/core/places");
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token(7L));
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		filter.doFilter(request, response, chain);

		assertThat(response.getStatus()).isEqualTo(200);
		HttpServletRequest forwarded = (HttpServletRequest) chain.getRequest();
		assertThat(forwarded).isNotNull();
		assertThat(forwarded.getHeader("X-User-Id")).isEqualTo("7");
	}

	@DisplayName("클라이언트가 보낸 X-User-Id는 제거하고 검증된 값으로 덮어쓴다")
	@Test
	void stripsClientSuppliedUserId() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/core/places");
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token(7L));
		request.addHeader("X-User-Id", "999"); // 위조 시도
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		filter.doFilter(request, response, chain);

		HttpServletRequest forwarded = (HttpServletRequest) chain.getRequest();
		assertThat(forwarded.getHeader("X-User-Id")).isEqualTo("7");
	}

	@DisplayName("토큰이 없으면 401을 반환하고 downstream으로 넘기지 않는다")
	@Test
	void missingTokenReturns401() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/link/links/extract");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		filter.doFilter(request, response, chain);

		assertThat(response.getStatus()).isEqualTo(401);
		assertThat(chain.getRequest()).isNull();
	}

	@DisplayName("유효하지 않은 토큰이면 401을 반환한다")
	@Test
	void invalidTokenReturns401() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/core/places");
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer not-a-real-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		filter.doFilter(request, response, chain);

		assertThat(response.getStatus()).isEqualTo(401);
		assertThat(chain.getRequest()).isNull();
	}

	@DisplayName("공개 경로(로그인)는 토큰 없이 통과하고 클라이언트 X-User-Id는 제거된다")
	@Test
	void publicPathPassesWithoutTokenAndStripsHeader() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/core/users/login");
		request.addHeader("X-User-Id", "999"); // 위조 시도
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		filter.doFilter(request, response, chain);

		assertThat(response.getStatus()).isEqualTo(200);
		HttpServletRequest forwarded = (HttpServletRequest) chain.getRequest();
		assertThat(forwarded.getHeader("X-User-Id")).isNull();
	}
}
