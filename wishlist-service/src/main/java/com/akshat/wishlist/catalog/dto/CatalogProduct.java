package com.akshat.wishlist.catalog.dto;

import java.math.BigDecimal;

public record CatalogProduct(
        Long id,
        String name,
        BigDecimal price
) {
}
