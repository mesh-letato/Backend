package com.pinmoa.core.space.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Space", description = "스페이스 관련 API")
@RestController
@RequestMapping("/api/core/spaces")
public class SpaceController {

	@Operation(summary = "스페이스 목록 조회")
	@GetMapping
	public ResponseEntity<String> getSpaces() {
		return ResponseEntity.ok("space list placeholder");
	}
}
