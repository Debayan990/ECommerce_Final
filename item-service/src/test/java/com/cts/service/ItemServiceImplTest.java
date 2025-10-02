package com.cts.service;

import com.cts.client.InventoryServiceClient;
import com.cts.dtos.ItemDto;
import com.cts.entities.Item;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.ItemRepository;
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
class ItemServiceImplTest {

    @Mock       //mock creates a "fake" version of a class
    private ItemRepository itemRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuditService auditService;

    @Mock
    private InventoryServiceClient inventoryServiceClient;

    @InjectMocks    //create a real instance of ItemServiceImpl and automatically inject the @Mock objects into it.
    private ItemServiceImpl itemService;

    private Item item1;
    private Item item2;
    private ItemDto itemDto1;
    private ItemDto itemDto2;

    @BeforeEach     //ensures method runs before every single test: creates fresh, consistent Item and ItemDto objects for each test case
    void init() {
        // Initialize common test objects
        item1 = new Item(1L, "Wireless Mouse", "Ergonomic wireless mouse", "Electronics", LocalDateTime.now());
        itemDto1 = new ItemDto(1L, "Wireless Mouse", "Ergonomic wireless mouse", "Electronics", item1.getCreatedAt());

        item2 = new Item(2L, "Keyboard", "Mechanical Keyboard", "Electronics", LocalDateTime.now());
        itemDto2 = new ItemDto(2L, "Keyboard", "Mechanical Keyboard", "Electronics", item2.getCreatedAt());
    }

    @Test
    void getAllItems() {
        when(itemRepository.findAll()).thenReturn(List.of(item1, item2));
        when(modelMapper.map(item1, ItemDto.class)).thenReturn(itemDto1);
        when(modelMapper.map(item2, ItemDto.class)).thenReturn(itemDto2);

        var result = itemService.getAllItems();

        assertEquals(2, result.size());
        assertEquals("Wireless Mouse", result.get(0).getName());
        verify(itemRepository).findAll();
    }

    @Test
    void getItemById() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(modelMapper.map(any(Item.class), eq(ItemDto.class))).thenReturn(itemDto1);

        var result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals("Wireless Mouse", result.getName());
        verify(itemRepository).findById(1L);      // It verifies that the method on our fake repository was actually called during the test
    }

    @Test
    void getItemById_NotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.getItemById(99L));
        verify(itemRepository).findById(99L);   //verify that the service tried to find the item
    }

    @Test
    void createItem() {
        when(modelMapper.map(any(ItemDto.class), eq(Item.class))).thenReturn(item1);
        when(itemRepository.save(any(Item.class))).thenReturn(item1);
        when(modelMapper.map(any(Item.class), eq(ItemDto.class))).thenReturn(itemDto1);
        doNothing().when(auditService).logEvent(anyString(), anyLong(), anyString(), anyString());

        ItemDto createdItem = itemService.createItem(itemDto1, "Bearer token");

        assertNotNull(createdItem);
        assertEquals("Wireless Mouse", createdItem.getName());
        verify(itemRepository).save(any(Item.class));
        verify(auditService).logEvent(eq("CREATE"), eq(1L), anyString(), anyString());
    }

    @Test
    void updateItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.save(any(Item.class))).thenReturn(item1);
        when(modelMapper.map(any(Item.class), eq(ItemDto.class))).thenReturn(itemDto1);
        doNothing().when(auditService).logEvent(anyString(), anyLong(), anyString(), anyString());

        ItemDto updatedItem = itemService.updateItem(1L, itemDto1, "Bearer token");

        assertNotNull(updatedItem);
        assertEquals("Wireless Mouse", updatedItem.getName());
        verify(itemRepository).findById(1L);
        verify(itemRepository).save(any(Item.class));
        verify(auditService).logEvent(eq("UPDATE"), eq(1L), anyString(), anyString());
    }

    @Test
    void updateItem_NotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.updateItem(99L, itemDto1, "Bearer token"));
        verify(itemRepository).findById(99L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void deleteItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        doNothing().when(itemRepository).delete(any(Item.class));
        doNothing().when(auditService).logEvent(anyString(), anyLong(), anyString(), anyString());
        doNothing().when(inventoryServiceClient).deleteInventoryByItemId(anyLong(), anyString());

        var result = itemService.deleteItem(1L, "Bearer token");

        assertNotNull(result);
        assertEquals("Item with ID 1 deleted successfully.", result);
        verify(itemRepository).findById(1L);
        verify(itemRepository).delete(item1);
        verify(auditService).logEvent(eq("DELETE"), eq(1L), anyString(), anyString());
    }

    @Test
    void deleteItem_NotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.deleteItem(99L, "Bearer token"));
        verify(itemRepository).findById(99L);
        verify(itemRepository, never()).delete(any(Item.class));    // Verify that delete was never called on any Item
    }
}