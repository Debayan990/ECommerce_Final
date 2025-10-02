package com.cts.controllers;

import com.cts.dtos.NotificationDto;
import com.cts.service.NotificationService;
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
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;       // A tool to simulate sending HTTP requests to the controller.

    // Creates a mock of the service layer to isolate the controller.
    @MockitoBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;      // A helper for converting Java objects to JSON.

    private NotificationDto notificationDto;

    // This method runs before each test to prepare fresh test data.
    @BeforeEach
    void init() {
        notificationDto = new NotificationDto(1L, "LowStock", "test@example.com", "Stock is low", LocalDateTime.now());
    }

    @Test
    void sendNotification() throws Exception {      //It Should Return 201_Created
        // Define the mock service's behavior for this test.
        when(notificationService.sendNotification(any(NotificationDto.class))).thenReturn(notificationDto);

        // Perform a POST request and check the HTTP response.
        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationDto)))
                .andExpect(status().isCreated())     // Assert that the status code is 201 CREATED.
                .andExpect(jsonPath("$.recipient").value("test@example.com"));    // Assert that the JSON response body contains the correct recipient.
    }

    @Test
    void getNotificationById() throws Exception {     //Should Return 200_OK
        // Tell the mock what to return when getNotificationById is called.
        when(notificationService.getNotificationById(1L)).thenReturn(notificationDto);

        // Perform a GET request and check the response.
        mockMvc.perform(get("/api/notifications/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAllNotifications() throws Exception {     //Should Return 200_OK
        // Arrange: Mock the service to return a list of notifications.
        when(notificationService.getAllNotifications()).thenReturn(List.of(notificationDto));

        // Act & Assert: Perform a GET request and check that the response is a list.
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1)); // Check that the list contains one item.
    }

    @Test
    void updateNotification() throws Exception {     //Should Return 200_OK
        // Arrange: Define the mock's behavior for the update operation.
        when(notificationService.updateNotification(eq(1L), any(NotificationDto.class))).thenReturn(notificationDto);

        // Act & Assert: Perform a PUT request and check the response.
        mockMvc.perform(put("/api/notifications/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Stock is low"));
    }

    @Test
    void deleteNotification() throws Exception {     //Should Return 200_OK
        // Define the mock's response for the delete operation.
        String successMessage = "Notification with ID 1 deleted successfully.";
        when(notificationService.deleteNotification(1L)).thenReturn(successMessage);

        //Perform a DELETE request and check the response body.
        mockMvc.perform(delete("/api/notifications/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage)); // Check for the plain text success message.
    }
}