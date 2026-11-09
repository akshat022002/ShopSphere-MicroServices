package com.akshat.review.review.controller;

import com.akshat.review.common.enums.Role;
import com.akshat.review.common.response.ApiResponse;
import com.akshat.review.review.dto.CreateReviewRequest;
import com.akshat.review.review.dto.ReviewResponse;
import com.akshat.review.review.dto.UpdateReviewRequest;
import com.akshat.review.review.service.ReviewService;
import com.akshat.review.security.userdetails.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

        private final ReviewService reviewService;
        private final CurrentUserProvider currentUserProvider;


        @PostMapping("/products/{productId}/reviews")
        @PreAuthorize("hasRole('CUSTOMER')")
        public ResponseEntity<ApiResponse<ReviewResponse>> createReview(@PathVariable Long productId,
                                                                        @RequestBody @Valid CreateReviewRequest request,
                                                                        Authentication authentication,
                                                                        @RequestHeader("Authorization") String token){
            Long userId = currentUserProvider.getUserId(authentication);

            ReviewResponse response = reviewService.createReview(userId, productId,request,token);

            ApiResponse<ReviewResponse> apiResponse = new ApiResponse<>(true,
                   "Review created successfully",
                    response,
                    LocalDateTime.now());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(apiResponse);

        }

        @GetMapping("/reviews/{reviewId}")
        public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(@PathVariable Long reviewId){

            ReviewResponse response = reviewService.getReviewById(reviewId);

            ApiResponse<ReviewResponse> apiResponse = new ApiResponse<>(true,
                    "Review fetched successfully",
                    response,
                    LocalDateTime.now());

            return ResponseEntity
                    .ok(apiResponse);

        }

        @GetMapping("/products/{productId}/reviews")
        public ResponseEntity<ApiResponse<List<ReviewResponse>>> getProductReviews(@PathVariable Long productId,
                                                                                   @RequestHeader("Authorization") String token)
                                                                                   {

            List<ReviewResponse> responses = reviewService.getProductReviews(productId,token);

            ApiResponse<List<ReviewResponse>> apiResponse = new ApiResponse<>(true,
                    "Product reviews fetched successfully",
                    responses,
                    LocalDateTime.now());

            return ResponseEntity
                    .ok(apiResponse);

        }


    @PutMapping("/reviews/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(@PathVariable Long reviewId,
                                                                    @RequestBody @Valid UpdateReviewRequest request,
                                                                    Authentication authentication){

        Long userId = currentUserProvider.getUserId(authentication);

        ReviewResponse response = reviewService.updateReview(userId,reviewId,request);

        ApiResponse<ReviewResponse> apiResponse = new ApiResponse<>(true,
                "Review updated successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity
                .ok(apiResponse);
    }

    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER','SELLER')")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId,
                                                          Authentication authentication,
                                                          @RequestHeader("Authorization") String token){

        Long userId = currentUserProvider.getUserId(authentication);
        Role role = currentUserProvider.getUserRole(authentication);

        reviewService.deleteReview(userId,role,reviewId,token);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Review soft deleted successfully",
                null,
                LocalDateTime.now());

        return ResponseEntity
                .ok(apiResponse);
    }


}
