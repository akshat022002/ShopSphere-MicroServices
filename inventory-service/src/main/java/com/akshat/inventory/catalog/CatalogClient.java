package com.akshat.inventory.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component

public class CatalogClient {

    private final RestClient restClient;

    public CatalogClient(@Qualifier("catalogRestClient") RestClient restClient){
        this.restClient=restClient;
    }

    public boolean productExists(Long productId,String token){

        return restClient.get()
                .uri("/api/products/{productId}",productId)
                .header("Authorization",token)
                .exchange((req,res)->
                        res.getStatusCode().is2xxSuccessful());
    }

    public boolean isProductOwnedBySeller(Long productId, Long sellerId,String token) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/{productId}/seller/{sellerId}")
                        .build(productId, sellerId))
                .header("Authorization",token)
                .retrieve()
                .body(Boolean.class);
    }
}
