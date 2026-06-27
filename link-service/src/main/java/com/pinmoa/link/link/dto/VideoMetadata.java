package com.pinmoa.link.link.dto;

/**
 * yt-dlp 로 추출한 동영상 메타데이터 중 장소 추출에 필요한 필드.
 */
public record VideoMetadata(
	String title,
	String description,
	String uploader,
	String webpageUrl
) {
    public String toExtractableText() {
        StringBuilder sb = new StringBuilder();
        if (title != null && !title.isBlank()) sb.append(title).append("\n");
        if (description != null && !description.isBlank()) sb.append(description);
        return sb.toString().strip();
    }
}
