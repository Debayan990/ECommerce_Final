package com.cts.service;

import com.cts.dtos.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, String token);
    ItemDto getItemById(Long id);
    List<ItemDto> getAllItems();
    ItemDto updateItem(Long id, ItemDto itemDto, String token);
    String deleteItem(Long id, String token);
}
