package com.akshat.dashboard.client;

import com.akshat.dashboard.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.math.BigDecimal;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @GetMapping("/api/payments/dashboard/admin")
    public ApiResponse<BigDecimal> getTotalRevenue(@RequestHeader("X-Service-Secret") String serviceSecret);

}
