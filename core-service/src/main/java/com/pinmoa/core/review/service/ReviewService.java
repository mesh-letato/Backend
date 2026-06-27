package com.pinmoa.core.review.service;

import com.pinmoa.core.review.dto.ReviewCreateRequest;
import com.pinmoa.core.review.dto.ReviewResponse;
import com.pinmoa.core.review.entity.Review;
import com.pinmoa.core.review.repository.ReviewRepository;
import com.pinmoa.core.user.domain.User;
import com.pinmoa.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponse createReview(Long userId, ReviewCreateRequest request) {
        Review review = Review.create(
                userId,
                request.placeId(),
                request.spaceId(),
                request.imageUrl(),
                request.content()
        );
        Review saved = reviewRepository.save(review);
        User user = userRepository.findById(userId).orElse(null);
        return ReviewResponse.of(saved, user);
    }

    public List<ReviewResponse> getReviewsByPlace(Long placeId) {
        return toResponsesWithUsers(reviewRepository.findByPlaceIdOrderByCreatedAtDesc(placeId));
    }

    public List<ReviewResponse> getReviewsBySpace(Long spaceId) {
        return toResponsesWithUsers(reviewRepository.findBySpaceIdOrderByCreatedAtDesc(spaceId));
    }

    private List<ReviewResponse> toResponsesWithUsers(List<Review> reviews) {
        List<Long> userIds = reviews.stream().map(Review::getUserId).distinct().toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        return reviews.stream()
                .map(r -> ReviewResponse.of(r, userMap.get(r.getUserId())))
                .toList();
    }
}
