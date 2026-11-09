package com.akshat.dashboard.client;

import com.akshat.dashboard.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "catalog-service")
public interface CatalogClient {

    @GetMapping("/api/products/dashboard/admin")
    ApiResponse<Long> getTotalProducts(@RequestHeader("X-Service-Secret") String serviceSecret);

}
