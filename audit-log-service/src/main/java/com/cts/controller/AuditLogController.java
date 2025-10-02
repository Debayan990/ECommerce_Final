package com.cts.controller;

import com.cts.dtos.AuditLogDto;
import com.cts.service.AuditLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PostMapping
    public ResponseEntity<AuditLogDto> logEvent(@Valid @RequestBody AuditLogDto auditLogDto) {
        return new ResponseEntity<>(auditLogService.logEvent(auditLogDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLogDto> getLogById(@PathVariable Long id) {
        return ResponseEntity.ok(auditLogService.getLogById(id));
    }

    @GetMapping
    public ResponseEntity<List<AuditLogDto>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuditLogDto> updateLog(@PathVariable Long id, @Valid @RequestBody AuditLogDto auditLogDto) {
        return ResponseEntity.ok(auditLogService.updateLog(id, auditLogDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLog(@PathVariable Long id) {
        return ResponseEntity.ok(auditLogService.deleteLog(id));
    }
}
