package com.cts.repository;

import com.cts.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByItemId(Long itemId);
    void deleteByItemId(Long itemId);
}
