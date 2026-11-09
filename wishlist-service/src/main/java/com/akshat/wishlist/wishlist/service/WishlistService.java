package com.akshat.wishlist.wishlist.service;


import com.akshat.wishlist.wishlist.dto.WishlistResponse;

public interface WishlistService {

    WishlistResponse getWishlist(Long userId,String token);

    WishlistResponse addToWishlist(Long userId, Long productId,String token);

    void removeFromWishlist(Long userId,Long productId);

    void clearWishlist(Long userId);
}
