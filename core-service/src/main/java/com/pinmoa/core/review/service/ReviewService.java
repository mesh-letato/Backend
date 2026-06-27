package com.pinmoa.core.review.service;

import com.pinmoa.core.review.dto.ReviewCreateRequest;
import com.pinmoa.core.review.dto.ReviewResponse;
import com.pinmoa.core.review.entity.Review;
import com.pinmoa.core.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public ReviewResponse createReview(Long userId, ReviewCreateRequest request) {
        Review review = Review.create(
                userId,
                request.placeId(),
                request.spaceId(),
                request.imageUrl(),
                request.content()
        );
        return ReviewResponse.from(reviewRepository.save(review));
    }

    public List<ReviewResponse> getReviewsByPlace(Long placeId) {
        return reviewRepository.findByPlaceIdOrderByCreatedAtDesc(placeId).stream()
                .map(ReviewResponse::from)
                .toList();
    }
}
