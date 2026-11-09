package com.akshat.catalog.audit.annotation;

import com.akshat.catalog.audit.enums.AuditAction;
import com.akshat.catalog.audit.enums.AuditEntityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Audit {

    AuditAction action();

    AuditEntityType entityType();

    String entityIdParam() default "";

}
