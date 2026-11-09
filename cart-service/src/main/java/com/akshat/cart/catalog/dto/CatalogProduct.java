package com.akshat.cart.catalog.dto;

import java.math.BigDecimal;

public record CatalogProduct(
        Long id,
        String name,
        BigDecimal price
) {
}
