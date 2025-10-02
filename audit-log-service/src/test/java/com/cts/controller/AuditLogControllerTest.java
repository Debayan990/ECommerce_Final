package com.cts.controller;

import com.cts.dtos.AuditLogDto;
import com.cts.service.AuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;     // A tool to simulate sending HTTP requests to the controller.

    // Creates a mock of the service layer to isolate the controller.
    @MockitoBean
    private AuditLogService auditLogService;

    @Autowired
    private ObjectMapper objectMapper;      // A helper for converting Java objects to JSON.

    private AuditLogDto auditLogDto;

    // Runs before each test to prepare the test data.
    @BeforeEach
    void init() {
        auditLogDto = new AuditLogDto(1L, "Item-Service", "CREATE", 101L, LocalDateTime.now(), "Item created");
    }

    @Test
    void logEvent() throws Exception {      //Should Return 201_Created
        // Define mock service behavior for logging an event.
        when(auditLogService.logEvent(any(AuditLogDto.class))).thenReturn(auditLogDto);

        // Perform POST request and check the response.
        mockMvc.perform(post("/api/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(auditLogDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.serviceName").value("Item-Service"));
    }

    @Test
    void getLogById() throws Exception {     //Should Return 200_OK
        when(auditLogService.getLogById(1L)).thenReturn(auditLogDto);

        mockMvc.perform(get("/api/logs/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAllLogs() throws Exception {      //Should Return 200_OK
        // Mock the service to return a list of logs.
        when(auditLogService.getAllLogs()).thenReturn(List.of(auditLogDto));

        // Perform GET request and check for a list in the response.
        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void updateLog() throws Exception {     //Should Return 200_OK
        when(auditLogService.updateLog(eq(1L), any(AuditLogDto.class))).thenReturn(auditLogDto);

        mockMvc.perform(put("/api/logs/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(auditLogDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("CREATE"));
    }

    @Test
    void deleteLog() throws Exception {     //Should Return 200_OK
        String successMessage = "AuditLog with ID 1 deleted successfully.";
        when(auditLogService.deleteLog(1L)).thenReturn(successMessage);

        mockMvc.perform(delete("/api/logs/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }
}