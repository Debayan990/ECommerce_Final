package com.cts.service;

import com.cts.dtos.AuditLogDto;
import com.cts.entities.AuditLog;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ModelMapper modelMapper;

    @Override
    public AuditLogDto logEvent(AuditLogDto auditLogDto) {
        AuditLog auditLog = modelMapper.map(auditLogDto, AuditLog.class);
        auditLog.setTimestamp(LocalDateTime.now());
        AuditLog savedLog = auditLogRepository.save(auditLog);
        return modelMapper.map(savedLog, AuditLogDto.class);
    }

    @Override
    public AuditLogDto getLogById(Long id) {
        AuditLog log = auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuditLog", "id", id));
        return modelMapper.map(log, AuditLogDto.class);
    }

    @Override
    public List<AuditLogDto> getAllLogs() {
        return auditLogRepository.findAll().stream()
                .map(log -> modelMapper.map(log, AuditLogDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public AuditLogDto updateLog(Long id, AuditLogDto auditLogDto) {
        AuditLog existingLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuditLog", "id", id));

        existingLog.setServiceName(auditLogDto.getServiceName());
        existingLog.setOperation(auditLogDto.getOperation());
        existingLog.setRecordId(auditLogDto.getRecordId());
        existingLog.setDetails(auditLogDto.getDetails());

        AuditLog updatedLog = auditLogRepository.save(existingLog);
        return modelMapper.map(updatedLog, AuditLogDto.class);
    }

    @Override
    public String deleteLog(Long id) {
        AuditLog log = auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuditLog", "id", id));
        auditLogRepository.delete(log);
        return "AuditLog with ID " + id + " deleted successfully.";
    }
}
