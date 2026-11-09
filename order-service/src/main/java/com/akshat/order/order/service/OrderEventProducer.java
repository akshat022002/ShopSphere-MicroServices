package com.akshat.order.order.service;

import com.akshat.order.order.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void publishOrderEvent(OrderEvent event){

        kafkaTemplate.send(TOPIC,event);

    }
}
