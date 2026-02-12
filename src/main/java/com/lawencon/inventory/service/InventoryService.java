package com.lawencon.inventory.service;

import com.lawencon.inventory.dto.InventoryRequest;
import com.lawencon.inventory.dto.InventoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {

    InventoryResponse save(InventoryRequest request);

    InventoryResponse getById(Long id);

    Page<InventoryResponse> findAll(Pageable pageable);
}
