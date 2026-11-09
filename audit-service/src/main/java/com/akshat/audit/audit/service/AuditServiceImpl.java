package com.akshat.audit.audit.service;

import com.akshat.audit.audit.entity.AuditLog;
import com.akshat.audit.audit.repository.AuditLogRepository;
import com.akshat.audit.dto.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class AuditServiceImpl implements AuditService{

    private final AuditLogRepository auditLogRepository;

    @Override
    public void saveAuditLog(AuditEvent event) {

        AuditLog auditLog = AuditLog.builder()
                .action(event.action())
                .entityId(event.entityId())
                .entityType(event.entityType())
                .userId(event.userId())
                .build();

        auditLogRepository.save(auditLog);

    }
}
