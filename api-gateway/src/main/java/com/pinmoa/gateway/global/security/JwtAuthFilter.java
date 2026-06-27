package com.pinmoa.gateway.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 게이트웨이 단일 인증 지점.
 * - 보호 경로는 JWT 를 검증하고, 통과 시 신뢰된 X-User-Id 헤더를 주입해 downstream(core/link)으로 전달한다.
 * - 클라이언트가 보낸 X-User-Id 는 항상 제거하여 위조를 차단한다.
 * - downstream 서비스는 게이트웨이를 우회해 직접 호출되지 않도록 네트워크에서 격리되어야 한다(EKS ClusterIP/NetworkPolicy).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	public static final String USER_ID_HEADER = "X-User-Id";

	private static final List<String> PUBLIC_PATHS = List.of(
		"/api/core/users/signup",
		"/api/core/users/login",
		"/api/core/users/refresh",
		"/swagger",
		"/swagger-ui",
		"/v3/api-docs",
		"/actuator",
		"/favicon.ico"
	);

	private final GatewayJwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		String path = request.getRequestURI();

		// 공개 경로: 인증 없이 통과하되, 클라이언트가 보낸 X-User-Id 는 제거한다.
		if (isPublic(path)) {
			filterChain.doFilter(new UserIdHeaderRequest(request, null), response);
			return;
		}

		String token = extractToken(request);
		if (!StringUtils.hasText(token)) {
			writeUnauthorized(response, "인증이 필요합니다.");
			return;
		}

		Long userId;
		try {
			userId = jwtUtil.getUserId(token);
		} catch (IllegalArgumentException e) {
			writeUnauthorized(response, "유효하지 않은 토큰입니다.");
			return;
		}

		filterChain.doFilter(new UserIdHeaderRequest(request, userId), response);
	}

	private boolean isPublic(String path) {
		return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
	}

	private String extractToken(HttpServletRequest request) {
		String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
			return bearer.substring(7);
		}
		return null;
	}

	private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"" + message + "\"}");
	}

	/**
	 * 클라이언트가 보낸 X-User-Id 를 숨기고, 검증된 userId(있으면)를 주입하는 요청 래퍼.
	 */
	static class UserIdHeaderRequest extends HttpServletRequestWrapper {

		private final String trustedUserId;

		UserIdHeaderRequest(HttpServletRequest request, Long userId) {
			super(request);
			this.trustedUserId = userId == null ? null : String.valueOf(userId);
		}

		@Override
		public String getHeader(String name) {
			if (USER_ID_HEADER.equalsIgnoreCase(name)) {
				return trustedUserId;
			}
			return super.getHeader(name);
		}

		@Override
		public Enumeration<String> getHeaders(String name) {
			if (USER_ID_HEADER.equalsIgnoreCase(name)) {
				return trustedUserId == null
					? Collections.emptyEnumeration()
					: Collections.enumeration(List.of(trustedUserId));
			}
			return super.getHeaders(name);
		}

		@Override
		public Enumeration<String> getHeaderNames() {
			List<String> names = new ArrayList<>();
			Enumeration<String> original = super.getHeaderNames();
			while (original.hasMoreElements()) {
				String name = original.nextElement();
				if (!USER_ID_HEADER.equalsIgnoreCase(name)) {
					names.add(name);
				}
			}
			if (trustedUserId != null) {
				names.add(USER_ID_HEADER);
			}
			return Collections.enumeration(names);
		}
	}
}
