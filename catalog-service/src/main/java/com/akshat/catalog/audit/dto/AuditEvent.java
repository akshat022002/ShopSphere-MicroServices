package com.akshat.catalog.audit.dto;

import com.akshat.catalog.audit.enums.AuditAction;
import com.akshat.catalog.audit.enums.AuditEntityType;

public record AuditEvent(
        AuditAction action,
        AuditEntityType entityType,
        Long userId,
        Long entityId
) {
}
