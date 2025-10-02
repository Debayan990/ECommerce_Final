package com.cts.controllers;

import com.cts.dtos.ItemDto;
import com.cts.service.ItemService;
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
class ItemControllerTest {

    // MockMvc is the main tool for simulating HTTP requests to controller
    @Autowired
    private MockMvc mockMvc;

    // We create a mock of the service layer because we only want to test the controller
    @MockitoBean
    private ItemService itemService;

    // ObjectMapper helps convert our Java objects to JSON strings for the request body
    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto1;
    private ItemDto itemDto2;

    @BeforeEach
    void init() {
        // This runs before each test to set up our test data
        itemDto1 = new ItemDto(1L, "Wireless Mouse", "A great mouse", "Electronics", LocalDateTime.now());
        itemDto2 = new ItemDto(2L, "Keyboard", "A great keyboard", "Electronics", LocalDateTime.now());
    }

    @Test
    void createItem() throws Exception {        //It Should Return 201_Created
        // Tell the mock service what to return when it's called
        when(itemService.createItem(any(ItemDto.class), any(String.class))).thenReturn(itemDto1);

        // Perform a POST request to the /api/items endpoint
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .header("Authorization", "Bearer token"))
                // Check the response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Wireless Mouse"));
    }

    @Test
    void getAllItems() throws Exception {       //It Should Return 200_OK And ListOfItems
        when(itemService.getAllItems()).thenReturn(List.of(itemDto1, itemDto2));

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Wireless Mouse"));
    }

    @Test
    void getItemById() throws Exception {       //It Should Return 200_OK And Item
        when(itemService.getItemById(1L)).thenReturn(itemDto1);

        mockMvc.perform(get("/api/items/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Wireless Mouse"));
    }

    @Test
    void updateItem() throws Exception {        //It Should Return 200_OK And UpdatedItem
        ItemDto updatedDto = new ItemDto(1L, "Updated Mouse", "Updated Desc", "Peripherals", LocalDateTime.now());
        when(itemService.updateItem(eq(1L), any(ItemDto.class), any(String.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/items/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto))
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Mouse"));
    }

    @Test
    void deleteItem() throws Exception {        //It Should Return 200_OK And Success Message
        String successMessage = "Item with ID 1 deleted successfully.";
        when(itemService.deleteItem(1L, "Bearer token")).thenReturn(successMessage);

        mockMvc.perform(delete("/api/items/{id}", 1L)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }
}