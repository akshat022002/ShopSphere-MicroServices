package com.akshat.order.order.service.restoration;

import com.akshat.order.order.entity.StockRestorationTask;
import com.akshat.order.order.enums.RestorationStatus;
import com.akshat.order.order.repository.StockRestorationTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StockRestorationTaskWriter {

    private final StockRestorationTaskRepository taskRepository;

    public void createTask(Long orderId,Long productId,Integer quantity,RuntimeException ex){

        StockRestorationTask task = StockRestorationTask.builder()
                .orderId(orderId)
                .productId(productId)
                .quantity(quantity)
                .status(RestorationStatus.PENDING)
                .attemptCount(0)
                .lastError(
                        ex.getMessage()==null
                        ?"Stock compensation failed"
                        :ex.getMessage().substring(0,Math.min(1000,ex.getMessage().length()))
                )
                .build();

        taskRepository.saveAndFlush(task);

        log.info("Stock restoration task created: orderId={}, productId={}",
                orderId, productId);


    }
}
