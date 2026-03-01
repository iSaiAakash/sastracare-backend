package com.academic.sastracare.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private static final Logger auditLog =
            LoggerFactory.getLogger("AUDIT");

    @AfterReturning("@annotation(audit)")
    public void logAudit(JoinPoint joinPoint,
                         Audit audit) {

        String methodName = joinPoint.getSignature().getName();

        auditLog.info("action={} method={}",
                audit.action(),
                methodName);
    }
}