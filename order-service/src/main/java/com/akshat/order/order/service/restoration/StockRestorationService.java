package com.akshat.order.order.service.restoration;

import com.akshat.order.order.entity.StockRestorationTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockRestorationService {

    private final InventoryRestorationRetryService retryService;
    private final StockRestorationTaskService stockRestorationTaskService;

    public void processTask(StockRestorationTask task){

        try{
            retryService.restoreStock(task);
            stockRestorationTaskService.markCompleted(task.getId());
        } catch (RuntimeException ex){

            try{
                stockRestorationTaskService.recordFailure(task.getId(),ex.getMessage());
            } catch(RuntimeException dbEx){

                log.error("Could not record Restoration failure for taskId :{}",
                        task.getId(),dbEx);

            }

            log.error("Stock restoration failed for orderId={}, productId={}",
                    task.getOrderId(),
                    task.getProductId(),ex);
        }

    }
}
