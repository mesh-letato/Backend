package com.pinmoa.core.review.service;

import com.pinmoa.core.review.dto.ReviewCreateRequest;
import com.pinmoa.core.review.dto.ReviewResponse;
import com.pinmoa.core.review.entity.Review;
import com.pinmoa.core.review.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	@InjectMocks
	private ReviewService reviewService;

	@DisplayName("후기를 생성하면 요청 값으로 매핑된 응답을 반환한다")
	@Test
	void createReview() {
		ReviewCreateRequest request = new ReviewCreateRequest(5L, 2L, "https://img/1.jpg", "트러플 파스타 인생맛 🤤");
		when(reviewRepository.save(any(Review.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		ReviewResponse response = reviewService.createReview(7L, request);

		assertThat(response.userId()).isEqualTo(7L);
		assertThat(response.placeId()).isEqualTo(5L);
		assertThat(response.spaceId()).isEqualTo(2L);
		assertThat(response.imageUrl()).isEqualTo("https://img/1.jpg");
		assertThat(response.content()).isEqualTo("트러플 파스타 인생맛 🤤");
	}

	@DisplayName("spaceId가 없어도 후기를 생성할 수 있다")
	@Test
	void createReviewWithoutSpace() {
		ReviewCreateRequest request = new ReviewCreateRequest(5L, null, "https://img/1.jpg", "맛있다");
		when(reviewRepository.save(any(Review.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		ReviewResponse response = reviewService.createReview(7L, request);

		assertThat(response.spaceId()).isNull();
		assertThat(response.placeId()).isEqualTo(5L);
	}

	@DisplayName("장소별 후기를 최신순 응답으로 반환한다")
	@Test
	void getReviewsByPlace() {
		when(reviewRepository.findByPlaceIdOrderByCreatedAtDesc(5L))
				.thenReturn(List.of(
						Review.create(7L, 5L, 2L, "https://img/1.jpg", "최고"),
						Review.create(8L, 5L, null, "https://img/2.jpg", "good")
				));

		List<ReviewResponse> reviews = reviewService.getReviewsByPlace(5L);

		assertThat(reviews).hasSize(2);
		assertThat(reviews).extracting(ReviewResponse::content).containsExactly("최고", "good");
	}
}
