package com.akshat.order.order.service.restoration;

import com.akshat.order.inventory.InventoryClient;
import com.akshat.order.order.entity.StockRestorationTask;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryRestorationRetryService {

    private final InventoryClient inventoryClient;

    @Retry(name = "inventoryRestore")
    public void restoreStock(StockRestorationTask task){

        inventoryClient.increaseStock(
                task.getProductId(),
                task.getQuantity(),
                task.getOrderId());

    }

}
