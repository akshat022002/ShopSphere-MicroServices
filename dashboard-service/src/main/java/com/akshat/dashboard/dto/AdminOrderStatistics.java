package com.akshat.dashboard.dto;

public record AdminOrderStatistics(
        long totalOrders,
        long pendingOrders,
        long cancelledOrders
) {
}
