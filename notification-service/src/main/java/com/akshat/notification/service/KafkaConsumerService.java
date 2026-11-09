package com.akshat.notification.service;

import com.akshat.notification.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationService notificationService;

    @KafkaListener(topics="order-events",
                   groupId = "notification-service")
    public void consumeMessage(OrderEvent event){

        log.info("Received order event: {}", event);
        notificationService.processOrderEvent(event);

    }
}
