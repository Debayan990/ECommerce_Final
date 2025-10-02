package com.cts.service;

import com.cts.dtos.InventoryDto;

import java.util.List;

public interface InventoryService {
    InventoryDto addInventory(InventoryDto inventoryDto, String token);
    InventoryDto getInventoryById(Long id);
    InventoryDto getInventoryByItemId(Long itemId);
    List<InventoryDto> getAllInventory();
    InventoryDto updateInventory(Long id, InventoryDto inventoryDto, String token);
    InventoryDto updateInventoryByItemId(Long itemId, InventoryDto inventoryDto, String token);
    String deleteInventory(Long id, String token);
    String deleteInventoryByItemId(Long itemId, String token);


}
