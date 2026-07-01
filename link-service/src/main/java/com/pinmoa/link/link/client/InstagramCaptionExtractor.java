package com.pinmoa.link.link.client;

import com.pinmoa.link.link.dto.InstaResult;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 인스타그램 게시물의 임베드 미리보기 페이지(/embed/captioned/)를 메타(페이스북) 외부 크롤러 UA로 조회해
 * 로그인 없이 캡션/작성자/썸네일을 추출한다.
 * 임베드 페이지는 로그인 세션 없이도 메타가 미리보기 카드용으로 내려주는 공개 HTML이며,
 * 마크업이 예고 없이 바뀔 수 있어 파싱 실패는 예외 대신 blocked=true 로 표현한다.
 */
@Slf4j
@Component
public class InstagramCaptionExtractor {

	private static final String CRAWLER_UA =
		"facebookexternalhit/1.1 (+http://www.facebook.com/externalhit_uatext.php)";

	private static final Pattern SHORTCODE_PATTERN =
		Pattern.compile("instagram\\.com/(?:p|reel|tv)/([A-Za-z0-9_-]+)");

	private final int timeoutMillis;

	public InstagramCaptionExtractor(@Value("${instagram.embed.timeout-seconds:10}") int timeoutSeconds) {
		this.timeoutMillis = timeoutSeconds * 1000;
	}

	public InstaResult fetch(String rawUrl) {
		String shortcode = extractShortcode(rawUrl.trim());
		if (shortcode == null) {
			return new InstaResult(null, false, "URL에서 게시물 ID(shortcode)를 찾지 못했습니다.");
		}

		String embedUrl = "https://www.instagram.com/p/" + shortcode + "/embed/captioned/";

		try {
			Connection.Response response = Jsoup.connect(embedUrl)
				.userAgent(CRAWLER_UA)
				.timeout(timeoutMillis)
				.ignoreHttpErrors(true)
				.execute();

			int status = response.statusCode();
			if (status != 200) {
				log.warn("인스타그램 임베드 페이지 응답 실패: shortcode={}, status={}", shortcode, status);
				return new InstaResult(null, true, "인스타그램 임베드 페이지 응답 실패 (status=" + status + ")");
			}

			return parse(response.parse());
		} catch (IOException e) {
			log.warn("인스타그램 임베드 페이지 조회 실패: {}", embedUrl, e);
			return new InstaResult(null, true, "인스타그램 임베드 페이지 조회 중 오류: " + e.getMessage());
		}
	}

	private InstaResult parse(Document doc) {
		String caption = text(doc, "div.Caption");

		if (caption == null || caption.isBlank()) {
			log.warn("인스타그램 캡션을 찾지 못함. 임베드 HTML: {}", doc.body().html());
			return new InstaResult(null, true,
				"캡션을 찾지 못했습니다. 비공개 게시물이거나 임베드 마크업이 변경되었을 수 있습니다.");
		}

		log.info("인스타그램 캡션 추출 성공: {}", caption);
		return new InstaResult(caption, false, null);
	}

	private String extractShortcode(String url) {
		Matcher matcher = SHORTCODE_PATTERN.matcher(url);
		return matcher.find() ? matcher.group(1) : null;
	}

	private String text(Document doc, String selector) {
		Element element = doc.selectFirst(selector);
		return element == null ? null : element.text();
	}
}