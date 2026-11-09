package com.akshat.review.catalog;

import com.akshat.review.catalog.dto.CatalogProduct;
import com.akshat.review.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CatalogClient {

    private final RestClient restClient;

    public CatalogClient(@Qualifier("catalogRestClient") RestClient restClient){
        this.restClient=restClient;
    }

    public CatalogProduct getProduct(Long productId,String token){

        ApiResponse<CatalogProduct> response = restClient.get()
                .uri("/api/products/{productId}",productId)
                .header("Authorization",token)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<CatalogProduct>>() {});

        return response.data();

    }
}
