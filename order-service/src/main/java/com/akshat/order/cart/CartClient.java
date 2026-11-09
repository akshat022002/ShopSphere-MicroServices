package com.akshat.order.cart;

import com.akshat.order.cart.dto.CartResponse;
import com.akshat.order.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
public class CartClient {

    private final RestClient restClient;

    public CartClient(@Qualifier("cartRestClient") RestClient restClient){

        this.restClient=restClient;

    }

    public CartResponse getCart(String token){

        ApiResponse<CartResponse> response = restClient.get()
                .uri("/api/cart")
                .header("Authorization",token)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<CartResponse>>(){});

        return response.data();
    }

    public void clearCart(String token){

        restClient.delete()
                .uri("/api/cart")
                .header("Authorization",token)
                .retrieve()
                .toBodilessEntity();
    }
}
