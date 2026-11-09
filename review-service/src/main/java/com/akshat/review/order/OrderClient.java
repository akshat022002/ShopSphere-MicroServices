package com.akshat.review.order;

import com.akshat.review.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OrderClient {

    private final RestClient restClient;

    public OrderClient(@Qualifier("orderRestClient") RestClient restClient){
        this.restClient=restClient;
    }

    public Boolean verifyPurchase(Long productId,String token){

        ApiResponse<Boolean> response = restClient.get()
                .uri("/api/orders/verify-purchase/{productId}",productId)
                .header("Authorization",token)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<Boolean>>() {});

        return response.data();

    }


}
