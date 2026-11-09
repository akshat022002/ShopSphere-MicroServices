package com.akshat.audit.dto;

import com.akshat.audit.audit.enums.AuditAction;
import com.akshat.audit.audit.enums.AuditEntityType;

public record AuditEvent(

    AuditAction action,
    AuditEntityType entityType,
    Long userId,
    Long entityId

) {
}
