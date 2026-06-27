package com.pinmoa.core.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification", description = "알림 관련 API")
@RestController
@RequestMapping("/api/core/notifications")
public class NotificationController {

	@Operation(summary = "알림 목록 조회")
	@GetMapping
	public ResponseEntity<String> getNotifications() {
		return ResponseEntity.ok("notification list placeholder");
	}
}
