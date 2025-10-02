package com.cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cts.entities.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
