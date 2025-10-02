package com.cts.service;

import com.cts.dtos.AuditLogDto;
import java.util.List;

public interface AuditLogService {
    AuditLogDto logEvent(AuditLogDto auditLogDto);
    AuditLogDto getLogById(Long id);
    List<AuditLogDto> getAllLogs();
    AuditLogDto updateLog(Long id, AuditLogDto auditLogDto);
    String deleteLog(Long id);
}
