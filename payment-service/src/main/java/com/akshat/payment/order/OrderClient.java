package com.akshat.payment.order;

import com.akshat.payment.common.response.ApiResponse;
import com.akshat.payment.order.dto.OrderResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OrderClient {

    private final RestClient restClient;

    @Value("${internal.order-service-secret}")
    private String serviceSecret;

    public OrderClient(@Qualifier("orderRestClient") RestClient restClient){
        this.restClient=restClient;
    }

    public OrderResponse getOrder(Long orderId,String token){

        ApiResponse<OrderResponse> response = restClient.get()
                .uri("/api/orders/{orderId}",orderId)
                .header("Authorization",token)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<OrderResponse>>() {});

        return response.data();

    }

    public void confirmOrder(Long orderId){

        restClient.patch()
                .uri("/api/orders/{orderId}/confirm",orderId)
                .header("X-Service-Secret",serviceSecret)
                .retrieve()
                .toBodilessEntity();

    }
}
