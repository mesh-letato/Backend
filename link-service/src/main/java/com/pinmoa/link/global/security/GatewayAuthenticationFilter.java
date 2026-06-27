package com.pinmoa.link.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 인증은 api-gateway 가 JWT 를 검증한 뒤 주입한 신뢰된 X-User-Id 헤더를 기반으로 한다.
 * 게이트웨이가 클라이언트의 X-User-Id 를 제거하고 검증된 값으로 주입하므로 이 값을 신뢰한다.
 *
 * 전제: link-service 는 게이트웨이를 우회해 직접 호출되지 않도록 네트워크에서 격리되어야 한다
 * (EKS ClusterIP + NetworkPolicy). 직접 호출이 가능하면 헤더 위조가 가능하다.
 */
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

	public static final String USER_ID_HEADER = "X-User-Id";

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String userIdHeader = request.getHeader(USER_ID_HEADER);
		if (StringUtils.hasText(userIdHeader)) {
			try {
				Long userId = Long.parseLong(userIdHeader.trim());
				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(userId, null, null);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (NumberFormatException ignored) {
				// 잘못된 헤더는 무시 — SecurityContext 가 비어있으면 인증 실패(401)로 처리됨
			}
		}
		filterChain.doFilter(request, response);
	}
}
