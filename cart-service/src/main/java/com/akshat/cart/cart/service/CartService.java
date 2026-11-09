package com.akshat.cart.cart.service;

import com.akshat.cart.cart.dto.AddToCartRequest;
import com.akshat.cart.cart.dto.CartResponse;
import com.akshat.cart.cart.dto.UpdateCartItemRequest;

public interface CartService {

    CartResponse getCart(Long userId, String token);

    CartResponse addToCart(
            Long userId,
            Long productId,
            AddToCartRequest request,
            String token
    );

    CartResponse updateCartItem(
            Long userId,
            Long productId,
            UpdateCartItemRequest request,
            String token
    );

    void removeCartItem(
            Long userId,
            Long productId
    );

    void clearCart(Long userId);
}