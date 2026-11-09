package com.akshat.order.order.dto;

public record AdminOrderStatistics(
        long totalOrders,
        long pendingOrders,
        long cancelledOrders
) {
}
