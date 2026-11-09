package com.akshat.coupon.coupon.dto;

import java.math.BigDecimal;
import java.util.List;

public record CouponCalculationRequest(
        List<Item> items
) {

    public record Item(
            Long productId,
            BigDecimal price,
            Integer quantity
    ){

    }
}
