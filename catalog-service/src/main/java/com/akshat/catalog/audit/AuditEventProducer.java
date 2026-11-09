package com.akshat.catalog.audit;

import com.akshat.catalog.audit.dto.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditEventProducer {

    private static final String TOPIC = "audit-events";

    private final KafkaTemplate<String ,AuditEvent> kafkaTemplate;

    public void publishAuditEvent(AuditEvent event){

        kafkaTemplate.send(TOPIC,event);

    }

}
