package com.cts.client;

import com.cts.dtos.AuditLogDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "audit-log-service", url = "${api.gateway.url}")
public interface AuditServiceClient {

    @PostMapping("/api/logs")
    void logEvent(@RequestBody AuditLogDto auditLogDto, @RequestHeader("Authorization") String token);
}
