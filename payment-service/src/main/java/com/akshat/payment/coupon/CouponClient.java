package com.akshat.payment.coupon;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
public class CouponClient {

    private final RestClient restClient;

    public CouponClient(@Qualifier("couponRestClient") RestClient restClient){
        this.restClient=restClient;
    }

    public void useCoupon(String code,String token){

         restClient.post()
                 .uri("/api/coupons/{code}/use",code)
                 .header("Authorization",token)
                 .retrieve()
                 .toBodilessEntity();
    }

}
