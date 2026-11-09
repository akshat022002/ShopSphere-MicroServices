package com.akshat.dashboard.dto;

public record AdminUserStatistics(
        long totalUsers,
        long totalSellers,
        long totalCustomers
) {
}
