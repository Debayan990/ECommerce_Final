package com.cts.service;

import com.cts.client.InventoryServiceClient;
import com.cts.dtos.ItemDto;
import com.cts.entities.Item;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;
    private final AuditService auditService;    // Inject the AuditService
    private final InventoryServiceClient inventoryServiceClient; // Inject the InventoryService

    @Override
    public ItemDto createItem(ItemDto itemDto, String token) {
        Item item = modelMapper.map(itemDto, Item.class);
        item.setCreatedAt(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);

        // Log in Audit
        auditService.logEvent("CREATE", savedItem.getId(), "Item created: " + savedItem.getName(), token);

        return modelMapper.map(savedItem, ItemDto.class);
    }

    @Override
    public ItemDto getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));
        return modelMapper.map(item, ItemDto.class);
    }

    @Override
    public List<ItemDto> getAllItems() {
        var items = itemRepository.findAll();
        return items.stream()
                .map(item -> modelMapper.map(item, ItemDto.class))
                .toList();
    }

    @Override
    public ItemDto updateItem(Long id, ItemDto itemDto, String token) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setCategory(itemDto.getCategory());

        Item updatedItem = itemRepository.save(item);

        // Log in Audit
        auditService.logEvent("UPDATE", updatedItem.getId(), "Item updated: " + updatedItem.getName(), token);

        return modelMapper.map(updatedItem, ItemDto.class);
    }

    @Override
    public String deleteItem(Long id, String token) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));
        itemRepository.delete(item);

        // Log in Audit
        auditService.logEvent("DELETE", id, "Item with ID " + id + " deleted.", token);

        // Delete the corresponding inventory of that item id
        try{
            inventoryServiceClient.deleteInventoryByItemId(id, token);

        } catch (Exception e) {
            System.err.println("Failed to delete inventory for item id"+id+" :"+e.getMessage());
        }

        return "Item with ID " + id + " deleted successfully.";
    }
}
