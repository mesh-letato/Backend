package com.pinmoa.link.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI", description = "AI 처리 관련 API")
@RestController
@RequestMapping("/api/link/ai")
public class AiController {

	@Operation(summary = "AI 처리 상태 확인")
	@GetMapping("/status")
	public ResponseEntity<String> getStatus() {
		return ResponseEntity.ok("ai status placeholder");
	}
}
