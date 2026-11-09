package com.akshat.dashboard.client;

import com.akshat.dashboard.common.response.ApiResponse;
import com.akshat.dashboard.dto.AdminUserStatistics;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/api/users/dashboard/admin")
    ApiResponse<AdminUserStatistics> getUserCount(@RequestHeader("X-Service-Secret") String serviceSecret);
}
