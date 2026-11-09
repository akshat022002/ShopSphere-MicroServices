package com.akshat.review.review.service;

import com.akshat.review.catalog.CatalogClient;
import com.akshat.review.catalog.dto.CatalogProduct;
import com.akshat.review.common.enums.Role;
import com.akshat.review.common.exception.BadRequestException;
import com.akshat.review.common.exception.DuplicateResourceException;
import com.akshat.review.common.exception.ResourceNotFoundException;
import com.akshat.review.order.OrderClient;
import com.akshat.review.review.dto.CreateReviewRequest;
import com.akshat.review.review.dto.ReviewResponse;
import com.akshat.review.review.dto.UpdateReviewRequest;
import com.akshat.review.review.entity.Review;
import com.akshat.review.review.mapper.ReviewMapper;
import com.akshat.review.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService{

    private final CatalogClient catalogClient;
    private final OrderClient orderClient;
    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;

    @Override
    public ReviewResponse createReview(Long userId, Long productId, CreateReviewRequest request, String token) {

        if(reviewRepository.existsByUserIdAndProductIdAndActiveTrue(userId,productId)){
            throw new DuplicateResourceException("Review already exists");
        }

         catalogClient.getProduct(productId,token);

         if(!orderClient.verifyPurchase(productId,token)){
             throw new BadRequestException("You can only review a product after it has been delivered");
         }

        Review review = Review.builder()
                .rating(request.rating())
                .comment(request.comment())
                .productId(productId)
                .userId(userId)
                .build();

         reviewRepository.save(review);

         return reviewMapper.toResponse(review);

    }

    @Transactional(readOnly = true)
    @Override
    public ReviewResponse getReviewById(Long reviewId) {

        return  reviewRepository.findById(reviewId)
                .filter(Review::isActive)
                .map(reviewMapper::toResponse)
                .orElseThrow(()-> new ResourceNotFoundException("Review not found with id : "+reviewId));

    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponse> getProductReviews(Long productId,String token) {

        catalogClient.getProduct(productId,token);

        return reviewRepository.findAllByProductIdAndActiveTrue(productId)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Override
    public ReviewResponse updateReview(Long userId, Long reviewId, UpdateReviewRequest request) {

        Review review = reviewRepository.findById(reviewId)
                .filter(Review::isActive)
                .orElseThrow(()->
                        new ResourceNotFoundException("Review not found with id : "+reviewId));


        if(!review.getUserId().equals(userId)){
            throw new AccessDeniedException("You are not authorized to update this review");
        }

        review.setRating(request.rating());
        if(request.comment()!=null){
            review.setComment(request.comment());
        }

        return reviewMapper.toResponse(review);

    }

    @Override
    public void deleteReview(Long userId, Role role, Long reviewId, String token) {

        Review review = reviewRepository.findById(reviewId)
                .filter(Review::isActive)
                .orElseThrow(()->
                        new ResourceNotFoundException("Review not found with id : "+reviewId));

        if (Role.ROLE_ADMIN.equals(role)) {
            review.setActive(false);
            return;
        }

        if (Role.ROLE_SELLER.equals(role)) {

            CatalogProduct product = catalogClient.getProduct(review.getProductId(), token);

            if (product.sellerId().equals(userId)) {
                review.setActive(false);
                return;
            }
        }

        if (Role.ROLE_CUSTOMER.equals(role)
                && review.getUserId().equals(userId)) {

            review.setActive(false);
            return;
        }

        throw new AccessDeniedException(
                "You are not authorized to delete this review");
    }
}
