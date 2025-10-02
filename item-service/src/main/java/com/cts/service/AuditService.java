package com.cts.service;

import com.cts.client.AuditServiceClient;
import com.cts.dtos.AuditLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditServiceClient auditServiceClient;

    public void logEvent(String operation, Long recordId, String details, String token) {
        try {
            AuditLogDto auditLog = new AuditLogDto();
            auditLog.setServiceName("item-service");
            auditLog.setOperation(operation);
            auditLog.setRecordId(recordId);
            auditLog.setDetails(details);
            auditServiceClient.logEvent(auditLog, token);
        } catch (Exception e) {
            // Log feign client error but don't fail the main transaction
            System.err.println("Error logging event to audit service: " + e.getMessage());
        }
    }
}
