package com.pinmoa.link.link.controller;

import com.pinmoa.link.link.dto.LinkExtractRequest;
import com.pinmoa.link.link.dto.LinkExtractResponse;
import com.pinmoa.link.link.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Link", description = "SNS 링크 추출 API")
@RestController
@RequestMapping("/api/link/links")
@RequiredArgsConstructor
public class LinkController {

	private final LinkService linkService;

	@Operation(summary = "링크에서 장소 후보 추출 (yt-dlp → LLM → 카카오맵)")
	@PostMapping("/extract")
	public ResponseEntity<LinkExtractResponse> extract(
			@RequestHeader("X-User-Id") Long userId,
			@Valid @RequestBody LinkExtractRequest request) {
		return ResponseEntity.ok(linkService.extract(userId, request));
	}
}
