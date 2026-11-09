package com.akshat.coupon.catalog;

import com.akshat.coupon.catalog.dto.CatalogProduct;
import com.akshat.coupon.common.response.ApiResponse;
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

    public boolean isProductOwnedBySeller(Long productId,Long sellerId, String token){

        return restClient.get()
                .uri("/api/products/{productId}/seller/{sellerId}",productId,sellerId)
                .header("Authorization",token)
                .retrieve()
                .body(Boolean.class);

    }

}
