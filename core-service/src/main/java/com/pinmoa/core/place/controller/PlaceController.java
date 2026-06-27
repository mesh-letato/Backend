package com.pinmoa.core.place.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Place", description = "장소 관련 API")
@RestController
@RequestMapping("/api/core/places")
public class PlaceController {

	@Operation(summary = "저장된 장소 목록 조회")
	@GetMapping
	public ResponseEntity<String> getPlaces() {
		return ResponseEntity.ok("place list placeholder");
	}
}
