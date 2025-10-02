package com.cts.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "inventory-service", url = "${api.gateway.url}")
public interface InventoryServiceClient {

    @DeleteMapping("/api/inventory/item/{itemId}")
    void deleteInventoryByItemId(@PathVariable("itemId") Long itemId, @RequestHeader("Authorization") String token);
}