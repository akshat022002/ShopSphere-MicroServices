package com.akshat.order.inventory;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
public class InventoryClient {

    private final RestClient restClient;

    @Value("${internal.inventory-service-secret}")
    private String serviceSecret;

    public InventoryClient(@Qualifier("inventoryRestClient") RestClient restClient){

        this.restClient=restClient;

    }

    @CircuitBreaker(name = "inventoryDecrease", fallbackMethod = "stockOperationFallback")
    public void decreaseStock(Long productId,Integer quantity, Long orderId){

        restClient.patch()
                .uri( uriBuilder -> uriBuilder
                                .path("/api/inventory/{productId}/decrease-for-order")
                                .queryParam("quantity",quantity)
                                .queryParam("orderId",orderId)
                                .build(productId)
                        )
                .header("X-Service-Secret",serviceSecret)
                .retrieve()
                .toBodilessEntity();

    }

    @CircuitBreaker(name = "inventoryIncrease",fallbackMethod = "stockOperationFallback")
    public void increaseStock(Long productId,Integer quantity,Long orderId){

        restClient.patch()
                .uri(uriBuilder -> uriBuilder
                                .path("/api/inventory/{productId}/increase-for-order")
                                .queryParam("quantity",quantity)
                                .queryParam("orderId",orderId)
                                .build(productId)
                )
                .header("X-Service-Secret",serviceSecret)
                .retrieve()
                .toBodilessEntity();

    }

    public void stockOperationFallback(Long productId, Integer quantity, Exception ex){
        throw new IllegalStateException(
                "Inventory Service is unavailable. Stock operation failed for productId: " + productId, ex);
    }

}
