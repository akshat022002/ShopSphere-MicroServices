package com.akshat.payment.order.dto;

import com.akshat.payment.order.enums.OrderStatus;

import java.math.BigDecimal;

public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal totalAmount,
        String couponCode
) {
}
