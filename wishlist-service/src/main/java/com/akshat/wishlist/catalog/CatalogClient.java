package com.akshat.wishlist.catalog;

import com.akshat.wishlist.catalog.dto.CatalogProduct;
import com.akshat.wishlist.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class CatalogClient {

    private final RestClient restClient;

    public CatalogProduct getProduct(Long productId,String token){

        ApiResponse<CatalogProduct> response = restClient.get()
                .uri("/api/products/{productId}",productId)
                .header("Authorization",token)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<CatalogProduct>>() {});

        return response.data();
    }
}
