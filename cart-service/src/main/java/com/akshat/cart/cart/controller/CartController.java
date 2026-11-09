package com.akshat.cart.cart.controller;


import com.akshat.cart.cart.dto.AddToCartRequest;
import com.akshat.cart.cart.dto.CartResponse;
import com.akshat.cart.cart.dto.UpdateCartItemRequest;
import com.akshat.cart.cart.service.CartService;
import com.akshat.cart.common.response.ApiResponse;
import com.akshat.cart.security.userdetails.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Authentication authentication,
                                                             @RequestHeader("Authorization") String token){


    Long userId = currentUserProvider.getUserId(authentication);

    CartResponse response = cartService.getCart(userId,token);

    ApiResponse<CartResponse> apiResponse = new ApiResponse<>(true,
            "Cart fetched successfully",
            response,
            LocalDateTime.now());

    return ResponseEntity.ok(apiResponse);

    }

    @PostMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(@PathVariable Long productId,
                                                               @Valid @RequestBody AddToCartRequest request, Authentication authentication,
                                                               @RequestHeader("Authorization") String token){


        Long userId = currentUserProvider.getUserId(authentication);

        CartResponse response = cartService.addToCart(userId,productId,request,token);

        ApiResponse<CartResponse> apiResponse = new ApiResponse<>(true,
                "Product added to cart successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @PatchMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(@PathVariable Long productId,
                                                                    @Valid @RequestBody UpdateCartItemRequest request,
                                                                    Authentication authentication,
                                                                    @RequestHeader("Authorization") String token){


        Long userId = currentUserProvider.getUserId(authentication);

        CartResponse response = cartService.updateCartItem(userId,productId,request,token);

        ApiResponse<CartResponse> apiResponse = new ApiResponse<>(true,
                "Cart item updated successfully",
                response,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(@PathVariable Long productId,
                                                            Authentication authentication){


        Long userId = currentUserProvider.getUserId(authentication);

        cartService.removeCartItem(userId,productId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Cart item removed successfully",
                null,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication){


        Long userId = currentUserProvider.getUserId(authentication);

        cartService.clearCart(userId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Cart cleared successfully",
                null,LocalDateTime.now());

        return  ResponseEntity.ok(apiResponse);
    }


}
