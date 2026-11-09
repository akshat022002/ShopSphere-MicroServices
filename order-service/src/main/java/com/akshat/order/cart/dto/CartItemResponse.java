package com.akshat.order.cart.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        Long productId,
        BigDecimal price,
        Integer quantity
) {
}
