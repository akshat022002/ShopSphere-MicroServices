package com.akshat.dashboard.client;

import com.akshat.dashboard.common.response.ApiResponse;
import com.akshat.dashboard.dto.AdminOrderStatistics;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/api/orders/dashboard/admin")
    public ApiResponse<AdminOrderStatistics> getOrderCount(@RequestHeader("X-Service-Secret") String serviceSecret);

}
