package com.akshat.cart.catalog;

import com.akshat.cart.catalog.dto.CatalogProduct;
import com.akshat.cart.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class CatalogClient {

    private final RestClient restClient;

    public CatalogProduct getProduct(Long productId,String token){

        ApiResponse<CatalogProduct> response =  restClient.get()
                .uri("/api/products/{productId}",productId)
                .header("Authorization",token)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<CatalogProduct>>() {});

        return response.data();
    }

}
