package com.akshat.wishlist.wishlist.dto;

import java.util.List;

public record WishlistResponse(
        Long wishlistId,
        List<WishlistItemResponse> items

) {
}
