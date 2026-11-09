package com.akshat.notification.dto;

import com.akshat.notification.enums.OrderEventType;

public record OrderEvent(
        OrderEventType eventType,
        Long orderId,
        Long userId,
        String customerEmail
) {
}
