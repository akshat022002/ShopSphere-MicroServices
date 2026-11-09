package com.akshat.audit.audit.service;

import com.akshat.audit.dto.AuditEvent;

public interface AuditService {

    void saveAuditLog(AuditEvent event);

}
