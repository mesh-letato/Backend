package com.pinmoa.core.user.controller;

import com.pinmoa.core.user.dto.UserSignupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "회원 관련 API")
@RestController
@RequestMapping("/api/core/users")
public class UserController {

	@Operation(summary = "회원가입")
	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody UserSignupRequest request) {
		return ResponseEntity.ok("signup placeholder");
	}

	@Operation(summary = "내 정보 조회")
	@GetMapping("/me")
	public ResponseEntity<String> getMyProfile() {
		return ResponseEntity.ok("my profile placeholder");
	}
}
