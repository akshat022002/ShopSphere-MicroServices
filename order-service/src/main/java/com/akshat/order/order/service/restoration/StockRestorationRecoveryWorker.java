package com.akshat.order.order.service.restoration;

import com.akshat.order.order.entity.StockRestorationTask;
import com.akshat.order.order.enums.RestorationStatus;
import com.akshat.order.order.repository.StockRestorationTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockRestorationRecoveryWorker {

    private final StockRestorationTaskRepository stockRestorationTaskRepository;
    private final StockRestorationService stockRestorationService;

    @Scheduled(fixedDelayString = "30000")
    public void recoverPendingTasks(){

        var tasks = stockRestorationTaskRepository.findByStatus(RestorationStatus.PENDING);

        for(var task : tasks){

            try{
                stockRestorationService.processTask(task);
            } catch (RuntimeException ex){

                log.error("Unexpected error processing restoration taskId={}",
                        task.getId(),
                        ex);

            }

        }

    }
}
