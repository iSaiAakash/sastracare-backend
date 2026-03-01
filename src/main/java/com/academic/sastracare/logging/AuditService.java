package com.academic.sastracare.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private static final Logger auditLog =
            LoggerFactory.getLogger("AUDIT");

    public void logAccess(String parentId,
                          String action,
                          String targetId) {

        auditLog.info("parentId={} action={} target={}",
                parentId, action, targetId);
    }
}