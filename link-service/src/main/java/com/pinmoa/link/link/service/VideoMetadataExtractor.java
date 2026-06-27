package com.pinmoa.link.link.service;

import com.pinmoa.link.link.dto.VideoMetadata;

/**
 * SNS 동영상 URL에서 메타데이터(설명글 등)를 추출한다.
 * 현재 구현은 yt-dlp CLI를 사용하며, 인터페이스로 분리해 추후 파이썬 사이드카 등으로 교체 가능하게 한다.
 */
public interface VideoMetadataExtractor {

	VideoMetadata extract(String url);
}
