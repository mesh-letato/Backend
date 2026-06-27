package com.pinmoa.core.review.controller;

import com.pinmoa.core.review.dto.ReviewCreateRequest;
import com.pinmoa.core.review.dto.ReviewResponse;
import com.pinmoa.core.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Review", description = "후기 관련 API")
@RestController
@RequestMapping("/api/core/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "후기 작성 (사진 + 한 줄 후기)")
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(userId, request));
    }

    @Operation(summary = "장소별 후기 목록 조회 (최신순)")
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviews(@RequestParam Long placeId) {
        return ResponseEntity.ok(reviewService.getReviewsByPlace(placeId));
    }
}
