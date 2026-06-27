package com.pinmoa.gateway.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 게이트웨이에서 JWT 액세스 토큰을 검증한다. core-service 의 JwtUtil 과 동일한 시크릿/서명 방식을 사용한다.
 */
@Component
public class GatewayJwtUtil {

	private final SecretKey key;

	public GatewayJwtUtil(@Value("${jwt.secret}") String secret) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 토큰을 검증하고 userId(subject)를 반환한다. 유효하지 않으면 IllegalArgumentException 을 던진다.
	 */
	public Long getUserId(String token) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
			return Long.parseLong(claims.getSubject());
		} catch (JwtException | IllegalArgumentException e) {
			throw new IllegalArgumentException("유효하지 않은 토큰입니다.", e);
		}
	}
}
