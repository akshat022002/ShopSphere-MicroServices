package com.akshat.order.catalog;

import com.akshat.order.catalog.dto.CatalogProduct;
import com.akshat.order.common.response.ApiResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class CatalogClient {

    private final RestClient restClient;

    public CatalogClient(@Qualifier("catalogRestClient") RestClient restClient){
        this.restClient=restClient;
    }

    @CircuitBreaker(name = "catalogService",fallbackMethod = "fallbackMethod")
    @Retry(name = "catalogService")
    public CatalogProduct getProduct(Long productId,String token){

        ApiResponse<CatalogProduct> response= restClient.get()
                .uri("/api/products/{productId}",productId)
                .header("Authorization",token)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<CatalogProduct>>() {
                });

        return response.data();

    }

    private CatalogProduct fallbackMethod(Long productId,String token,Exception ex){

       if(ex instanceof CallNotPermittedException){
           log.warn("Catalog Circuit Breaker is OPEN for productId={}",productId);
       }

        throw new IllegalStateException("Unable to retrieve product details",ex);

    }

}
