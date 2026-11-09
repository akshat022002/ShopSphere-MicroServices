package com.akshat.ai.catalog;

import com.akshat.ai.catalog.dto.ProductSearchResponse;
import com.akshat.ai.common.response.ApiResponse;
import com.akshat.ai.ai.dto.ProductAiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CatalogClient {

    private final RestClient restClient;

    public List<ProductAiResponse> searchProducts(String name,
                                                  BigDecimal maxPrice,
                                                  BigDecimal minPrice,
                                                  String token){


        ApiResponse<ProductSearchResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products")
                        .queryParam("name", name)
                        .queryParam("minPrice", minPrice)
                        .queryParam("maxPrice", maxPrice)
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .queryParam("sortBy", "createdAt")
                        .queryParam("direction", "desc")
                        .build())
                .header("Authorization", token)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<ProductSearchResponse>>() {});

    return response.data().content();
    }
}
