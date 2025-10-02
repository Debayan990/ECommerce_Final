package com.cts.controllers;

import com.cts.dtos.InventoryDto;
import com.cts.service.InventoryService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryControllerTest {

    // MockMvc is the main tool for simulating HTTP requests to controller
    @Autowired
    private MockMvc mockMvc;

    // Created a mock of the service layer because we only want to test the controller
    @MockitoBean
    private InventoryService inventoryService;

    @Autowired
    private ObjectMapper objectMapper;      // ObjectMapper helps convert our Java objects to JSON strings for the request body

    private InventoryDto inventoryDto;

    @BeforeEach
    void init() {
        // This runs before each test to set up our test data
        inventoryDto = new InventoryDto(1L, 101L, 50, "A1-North", LocalDateTime.now());
    }

    @Test
    void addInventory() throws Exception {      //It Should Return 201_Created
        when(inventoryService.addInventory(any(InventoryDto.class), any(String.class))).thenReturn(inventoryDto);

        // Act & Assert
        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventoryDto))
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(101L));
    }

    @Test
    void addInventory_WithInvalidData() throws Exception {      //It Should Return 400_BadRequest
        // DTO with a null itemId to trigger validation
        InventoryDto invalidDto = new InventoryDto(null, null, 50, "A1-North", null);


        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllInventory() throws Exception {       //It Should Return 200_OK And List
        when(inventoryService.getAllInventory()).thenReturn(List.of(inventoryDto));


        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].warehouseLocation").value("A1-North"));
    }

    @Test
    void getInventoryById() throws Exception {      //It Should Return 200_OK
        when(inventoryService.getInventoryById(1L)).thenReturn(inventoryDto);


        mockMvc.perform(get("/api/inventory/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateInventory() throws Exception {       //It Should Return 200_OK
        when(inventoryService.updateInventory(eq(1L), any(InventoryDto.class), any(String.class))).thenReturn(inventoryDto);


        mockMvc.perform(put("/api/inventory/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventoryDto))
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    void updateInventoryByItemId() throws Exception { //Should Return 200_OK
        when(inventoryService.updateInventoryByItemId(eq(101L), any(InventoryDto.class), anyString())).thenReturn(inventoryDto);

        mockMvc.perform(put("/api/inventory/item/{itemId}", 101L)
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(101L));
    }

    @Test
    void deleteInventory() throws Exception {       //It Should Return 200_OK
        String successMessage = "Inventory with ID 1 deleted successfully.";
        when(inventoryService.deleteInventory(1L, "Bearer token")).thenReturn(successMessage);


        mockMvc.perform(delete("/api/inventory/{id}", 1L)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }

    @Test
    void deleteInventoryByItemId() throws Exception { //Should Return 200_OK
        String successMessage = "Inventory with Item ID 101 deleted successfully.";     // Define the success message

        when(inventoryService.deleteInventoryByItemId(eq(101L), anyString())).thenReturn(successMessage);    // Mocking service to return the success message

        // Performing DELETE request and check the response
        mockMvc.perform(delete("/api/inventory/item/{itemId}", 101L)
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }
}