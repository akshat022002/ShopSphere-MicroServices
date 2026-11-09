package com.akshat.audit.kafka;

import com.akshat.audit.audit.service.AuditService;
import com.akshat.audit.dto.AuditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final AuditService auditService;

    @KafkaListener(topics = "audit-events",groupId = "audit-service")
    public void consume(AuditEvent event){

        log.info("Received audit Kafka record: {}", event);
        auditService.saveAuditLog(event);

    }
}
