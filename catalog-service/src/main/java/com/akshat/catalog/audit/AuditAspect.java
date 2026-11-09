package com.akshat.catalog.audit;

import com.akshat.catalog.audit.annotation.Audit;
import com.akshat.catalog.audit.dto.AuditEvent;
import com.akshat.catalog.audit.dto.AuditableResponse;
import com.akshat.catalog.security.userprovider.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Aspect
public class AuditAspect {

    private final CurrentUserProvider currentUserProvider;
    private final AuditEventProducer eventProducer;

    @AfterReturning(pointcut = "@annotation(audit)",returning = "result")
    public void audit(JoinPoint joinPoint, Object result, Audit audit){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Long userId = currentUserProvider.getUserId(authentication);

        Long entityId = extractEntityId(joinPoint,audit.entityIdParam(),result);

        AuditEvent event = new AuditEvent(audit.action(),audit.entityType(),userId,entityId);

        eventProducer.publishAuditEvent(event);

    }

    private Long extractEntityId(JoinPoint joinPoint,String entityIdParam,Object result){

        if(!entityIdParam.isBlank()){

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();

            String[] parameterNames = signature.getParameterNames();
            Object[] arguments = joinPoint.getArgs();

            for(int i =0;i<arguments.length;i++){

                if(entityIdParam.equals(parameterNames[i]) && arguments[i] instanceof Long id){
                    return id;
                }

            }

        }

        if(result instanceof AuditableResponse response){
            return response.id();
        }

        return null;
    }
}
