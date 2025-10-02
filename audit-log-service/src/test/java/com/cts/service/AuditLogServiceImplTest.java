package com.cts.service;

import com.cts.dtos.AuditLogDto;
import com.cts.entities.AuditLog;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuditLogServiceImplTest {

    // Creates a mock of the repository to isolate the service from the database.
    @Mock
    private AuditLogRepository auditLogRepository;

    // Creates a mock for the ModelMapper utility.
    @Mock
    private ModelMapper modelMapper;

    // Creates a real service instance and injects the defined mocks.
    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    private AuditLog auditLog;
    private AuditLogDto auditLogDto;

    // This method runs before each test to provide fresh test data
    @BeforeEach
    void init() {
        auditLog = new AuditLog(1L, "Item-Service", "CREATE", 101L, LocalDateTime.now(), "Item created");
        auditLogDto = new AuditLogDto(1L, "Item-Service", "CREATE", 101L, auditLog.getTimestamp(), "Item created");
    }

    @Test
    void logEvent() {
        // Define mock behavior for creating a log
        when(modelMapper.map(any(AuditLogDto.class), eq(AuditLog.class))).thenReturn(auditLog);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);
        when(modelMapper.map(any(AuditLog.class), eq(AuditLogDto.class))).thenReturn(auditLogDto);

        // Call the service method.
        AuditLogDto result = auditLogService.logEvent(auditLogDto);

        // Verify the result and that the save method was called.
        assertNotNull(result);
        assertEquals("Item-Service", result.getServiceName());
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void getLogById() {
        // Mock the repository to return a log when findById is called.
        when(auditLogRepository.findById(1L)).thenReturn(Optional.of(auditLog));
        when(modelMapper.map(any(AuditLog.class), eq(AuditLogDto.class))).thenReturn(auditLogDto);

        // Call the service method.
        AuditLogDto result = auditLogService.getLogById(1L);

        // Verify the correct data was returned.
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(auditLogRepository).findById(1L);
    }

    @Test
    void getLogById_NotFound() {
        // Simulate that the log is not found.
        when(auditLogRepository.findById(99L)).thenReturn(Optional.empty());

        // Verify that the correct exception is thrown.
        assertThrows(ResourceNotFoundException.class, () -> auditLogService.getLogById(99L));
    }

    @Test
    void getAllLogs() {
        // Mock the repository to return a list of logs.
        when(auditLogRepository.findAll()).thenReturn(List.of(auditLog));
        when(modelMapper.map(any(AuditLog.class), eq(AuditLogDto.class))).thenReturn(auditLogDto);

        // Call the service method.
        List<AuditLogDto> results = auditLogService.getAllLogs();

        // Check the size of the returned list.
        assertEquals(1, results.size());
        verify(auditLogRepository).findAll();
    }

    @Test
    void updateLog() {
        // Mock finding and saving the log for an update.
        when(auditLogRepository.findById(1L)).thenReturn(Optional.of(auditLog));
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);
        when(modelMapper.map(any(AuditLog.class), eq(AuditLogDto.class))).thenReturn(auditLogDto);

        // Call the update method.
        AuditLogDto result = auditLogService.updateLog(1L, auditLogDto);

        // Ensure the result is correct and save was called.
        assertNotNull(result);
        verify(auditLogRepository).save(auditLog);
    }

    @Test
    void updateLog_NotFound() {
        // Simulate that the log to update is not found.
        when(auditLogRepository.findById(99L)).thenReturn(Optional.empty());

        // Verify an exception is thrown and save is never called.
        assertThrows(ResourceNotFoundException.class, () -> auditLogService.updateLog(99L, auditLogDto));
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void deleteLog() {
        // Mock the repository to find the log to be deleted.
        when(auditLogRepository.findById(1L)).thenReturn(Optional.of(auditLog));
        doNothing().when(auditLogRepository).delete(auditLog); // Specify that the delete method does nothing.

        // Call the delete method.
        String result = auditLogService.deleteLog(1L);

        // Check the success message and verify the delete method was called.
        assertEquals("AuditLog with ID 1 deleted successfully.", result);
        verify(auditLogRepository).delete(auditLog);
    }

    @Test
    void deleteLog_NotFound() {
        // Simulate that the log to delete is not found.
        when(auditLogRepository.findById(99L)).thenReturn(Optional.empty());

        // Verify an exception is thrown and delete is never called.
        assertThrows(ResourceNotFoundException.class, () -> auditLogService.deleteLog(99L));
        verify(auditLogRepository, never()).delete(any(AuditLog.class));
    }
}