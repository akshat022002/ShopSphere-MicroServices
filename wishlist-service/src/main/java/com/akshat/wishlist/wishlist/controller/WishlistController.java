package com.akshat.wishlist.wishlist.controller;

import com.akshat.wishlist.common.response.ApiResponse;
import com.akshat.wishlist.security.userdetails.CurrentUserProvider;
import com.akshat.wishlist.wishlist.dto.WishlistResponse;
import com.akshat.wishlist.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<ApiResponse<WishlistResponse>> getWishlist(Authentication authentication,
                                                                     @RequestHeader("Authorization")
                                                                     String token){

    Long userId = currentUserProvider.getUserId(authentication);

    WishlistResponse response = wishlistService.getWishlist(userId,token);

    ApiResponse<WishlistResponse> apiResponse = new ApiResponse<>(true,
            "Wishlist fetched successfully",
            response,
            LocalDateTime.now());

    return ResponseEntity.ok(apiResponse);

    }

    @PostMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(@PathVariable Long productId,
                                                                       Authentication authentication,
                                                                       @RequestHeader("Authorization")
                                                                       String token
                                                                       ){


        Long userId = currentUserProvider.getUserId(authentication);

        WishlistResponse response = wishlistService.addToWishlist(userId,productId,token);

        ApiResponse<WishlistResponse> apiResponse = new ApiResponse<>(true,
                "Product added to wishlist successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(@PathVariable Long productId,
                                                                Authentication authentication){


        Long userId = currentUserProvider.getUserId(authentication);

        wishlistService.removeFromWishlist(userId,productId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Product removed from wishlist successfully",
                null,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearWishlist(Authentication authentication){

        Long userId = currentUserProvider.getUserId(authentication);

        wishlistService.clearWishlist(userId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Wishlist cleared  successfully",
                null,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

}
