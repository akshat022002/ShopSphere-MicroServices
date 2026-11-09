package com.akshat.auth.dashboard.dto;

public record AdminUserStatistics(
        long totalUsers,
        long totalSellers,
        long totalCustomers
) {
}
