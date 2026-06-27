package com.pinmoa.core.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Review", description = "후기 관련 API")
@RestController
@RequestMapping("/api/core/reviews")
public class ReviewController {

	@Operation(summary = "후기 목록 조회")
	@GetMapping
	public ResponseEntity<String> getReviews() {
		return ResponseEntity.ok("review list placeholder");
	}
}
