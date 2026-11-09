package com.akshat.audit.audit.entity;

import com.akshat.audit.audit.enums.AuditAction;
import com.akshat.audit.audit.enums.AuditEntityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "audit_logs",
       indexes = {
        @Index(name = "idx_audit_user",columnList = "user_id"),
        @Index(name = "idx_audit_created_at",columnList = "created_at")
       })
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditEntityType entityType;

    private Long userId;

    private Long entityId;

    @Builder.Default
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt=LocalDateTime.now();

}
