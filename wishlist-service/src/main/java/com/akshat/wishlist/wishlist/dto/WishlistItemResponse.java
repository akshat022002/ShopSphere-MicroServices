package com.akshat.wishlist.wishlist.dto;

import java.math.BigDecimal;

public record WishlistItemResponse(
        Long id,
        Long productId,
        String productName,
        BigDecimal price
) {
}
