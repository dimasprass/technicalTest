package com.lawencon.inventory.repository;

import com.lawencon.inventory.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Page<Inventory> findAll(Pageable pageable);
}
