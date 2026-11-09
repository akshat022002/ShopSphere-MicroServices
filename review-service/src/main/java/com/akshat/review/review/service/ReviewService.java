package com.akshat.review.review.service;


import com.akshat.review.common.enums.Role;
import com.akshat.review.review.dto.CreateReviewRequest;
import com.akshat.review.review.dto.ReviewResponse;
import com.akshat.review.review.dto.UpdateReviewRequest;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(Long userId, Long productId, CreateReviewRequest request,String token);

    ReviewResponse getReviewById(Long reviewId);

    List<ReviewResponse> getProductReviews(Long productId,String token);

    ReviewResponse updateReview(Long userId, Long reviewId, UpdateReviewRequest request);

    void deleteReview(Long userId, Role role, Long reviewId,String token);
}
