package com.lawencon.inventory.service.impl;

import com.lawencon.inventory.dto.InventoryRequest;
import com.lawencon.inventory.dto.InventoryResponse;
import com.lawencon.inventory.entity.Inventory;
import com.lawencon.inventory.entity.Item;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.repository.InventoryRepository;
import com.lawencon.inventory.repository.ItemRepository;
import com.lawencon.inventory.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, ItemRepository itemRepository) {
        this.inventoryRepository = inventoryRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public InventoryResponse save(InventoryRequest request) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + request.getItemId()));
        Inventory inventory = new Inventory();
        inventory.setItem(item);
        inventory.setQuantity(request.getQuantity());
        inventory.setType(Inventory.Type.valueOf(request.getType().toUpperCase()));
        inventory = inventoryRepository.save(inventory);
        return toResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
        return toResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryResponse> findAll(Pageable pageable) {
        return inventoryRepository.findAll(pageable).map(this::toResponse);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        InventoryResponse response = new InventoryResponse();
        response.setId(inventory.getId());
        response.setItemId(inventory.getItem().getId());
        response.setItemName(inventory.getItem().getName());
        response.setQuantity(inventory.getQuantity());
        response.setType(inventory.getType().name());
        return response;
    }
}
