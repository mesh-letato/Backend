package com.pinmoa.link.link.controller;

import com.pinmoa.link.link.dto.LinkExtractRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Link", description = "SNS 링크 추출 API")
@RestController
@RequestMapping("/api/link/links")
public class LinkController {

	@Operation(summary = "링크에서 장소 후보 추출")
	@PostMapping("/extract")
	public ResponseEntity<String> extract(@RequestBody LinkExtractRequest request) {
		return ResponseEntity.ok("link extract placeholder");
	}
}
