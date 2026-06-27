package com.pinmoa.link.link.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 링크 추출 요청 이력. 어떤 사용자가 어떤 SNS 링크로 추출을 요청했는지 기록한다.
 */
@Entity
@Table(name = "sns_links")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SnsLink {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(nullable = false, length = 1000)
	private String url;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Platform platform;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	private SnsLink(Long userId, String url, Platform platform) {
		this.userId = userId;
		this.url = url;
		this.platform = platform;
	}

	public static SnsLink create(Long userId, String url, Platform platform) {
		return new SnsLink(userId, url, platform);
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}
