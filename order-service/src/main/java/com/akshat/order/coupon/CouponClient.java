package com.akshat.order.coupon;

import com.akshat.order.common.response.ApiResponse;
import com.akshat.order.coupon.dto.CouponCalculationRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class CouponClient {

    private final RestClient restClient;

    public CouponClient(@Qualifier("couponRestClient") RestClient restClient){
        this.restClient=restClient;
    }

    public BigDecimal calculateDiscount(String code, CouponCalculationRequest request,String token){

        ApiResponse<BigDecimal> response = restClient.post()
                .uri("/api/coupons/calculate?code={code}",code)
                .header("Authorization",token)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<BigDecimal>>() {});

        return response.data();
    }
}
