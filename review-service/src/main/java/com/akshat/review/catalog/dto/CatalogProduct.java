package com.akshat.review.catalog.dto;

import java.math.BigDecimal;

public record CatalogProduct(
        Long id,
        String name,
        BigDecimal price,
        Long sellerId
) {
}
