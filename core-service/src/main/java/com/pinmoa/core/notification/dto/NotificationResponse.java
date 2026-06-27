package com.pinmoa.core.notification.dto;

public record NotificationResponse(
	Long id,
	String type,
	String message,
	boolean isRead
) {
}
