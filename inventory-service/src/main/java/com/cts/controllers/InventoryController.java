package com.cts.controllers;

import com.cts.dtos.InventoryDto;
import com.cts.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryDto> addInventory(@Valid @RequestBody InventoryDto inventoryDto, @RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(inventoryService.addInventory(inventoryDto, token), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryDto> getInventoryById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }


    @GetMapping("/item/{itemId}")
    public ResponseEntity<InventoryDto> getInventoryByItemId(@PathVariable Long itemId) {
        return ResponseEntity.ok(inventoryService.getInventoryByItemId(itemId));
    }

    @GetMapping
    public ResponseEntity<List<InventoryDto>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }


    @PutMapping("/{id}")
    public ResponseEntity<InventoryDto> updateInventory(@PathVariable Long id, @Valid @RequestBody InventoryDto inventoryDto, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, inventoryDto, token));
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<InventoryDto> updateInventoryByItemId(@PathVariable Long itemId, @Valid @RequestBody InventoryDto inventoryDto, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(inventoryService.updateInventoryByItemId(itemId, inventoryDto, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInventory(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(inventoryService.deleteInventory(id, token));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<String> deleteInventoryByItemId(@PathVariable Long itemId, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(inventoryService.deleteInventoryByItemId(itemId, token));
    }
}
