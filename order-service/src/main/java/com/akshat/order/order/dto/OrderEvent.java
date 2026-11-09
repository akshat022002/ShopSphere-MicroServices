package com.akshat.order.order.dto;

import com.akshat.order.order.enums.OrderEventType;

public record OrderEvent(
    OrderEventType eventType,
    Long orderId,
    Long userId,
    String customerEmail
) {
}
