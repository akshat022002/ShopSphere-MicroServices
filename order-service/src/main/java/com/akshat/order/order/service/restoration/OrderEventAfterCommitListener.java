package com.akshat.order.order.service.restoration;

import com.akshat.order.order.dto.OrderEvent;
import com.akshat.order.order.service.OrderEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventAfterCommitListener {

    private final OrderEventProducer orderEventProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderEvent(OrderEvent event){

        log.info("Publishing order event after transaction commit");
        orderEventProducer.publishOrderEvent(event);

    }
}
