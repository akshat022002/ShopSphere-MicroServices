package com.akshat.order.order.dto;

import com.akshat.order.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long id,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
}
