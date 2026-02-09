package com.lawencon.inventory.service;

import com.lawencon.inventory.dto.InventoryRequest;
import com.lawencon.inventory.dto.InventoryResponse;
import com.lawencon.inventory.dto.PageResponse;
import com.lawencon.inventory.entity.Inventory;
import com.lawencon.inventory.entity.Inventory.Type;
import com.lawencon.inventory.entity.Item;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.repository.InventoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ItemService itemService;

    public InventoryService(InventoryRepository inventoryRepository, ItemService itemService) {
        this.inventoryRepository = inventoryRepository;
        this.itemService = itemService;
    }

    public InventoryResponse getById(Long id) {
        Inventory inv = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", id));
        return toResponse(inv);
    }

    public PageResponse<InventoryResponse> list(Pageable pageable) {
        Page<Inventory> page = inventoryRepository.findAll(pageable);
        var content = page.getContent().stream().map(this::toResponse).toList();
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    @Transactional
    public InventoryResponse save(InventoryRequest request) {
        Item item = itemService.getEntityById(request.getItemId());
        int currentStock = item.getStock();

        // For withdrawal, validate stock before persisting
        if (request.getType() == Type.W) {
            if (currentStock < request.getQuantity()) {
                throw new com.lawencon.inventory.exception.InsufficientStockException(
                        "Insufficient stock for withdrawal. Item: " + item.getName() + ", available: " + currentStock + ", requested: " + request.getQuantity());
            }
        }

        Inventory inv = new Inventory();
        inv.setItem(item);
        inv.setType(request.getType());
        inv.setQuantity(request.getQuantity());
        inv = inventoryRepository.save(inv);

        // Apply stock: T = increase, W = decrease
        if (request.getType() == Type.T) {
            item.setStock(currentStock + request.getQuantity());
        } else {
            item.setStock(currentStock - request.getQuantity());
        }
        itemService.updateStock(item);

        return toResponse(inv);
    }

    @Transactional
    public InventoryResponse edit(Long id, InventoryRequest request) {
        Inventory inv = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", id));
        Item item = itemService.getEntityById(request.getItemId());

        // Revert previous stock effect
        int prevQty = inv.getQuantity();
        Type prevType = inv.getType();
        Item prevItem = inv.getItem();
        if (prevType == Type.T) {
            prevItem.setStock(prevItem.getStock() - prevQty);
        } else {
            prevItem.setStock(prevItem.getStock() + prevQty);
        }
        itemService.updateStock(prevItem);

        inv.setItem(item);
        inv.setType(request.getType());
        inv.setQuantity(request.getQuantity());
        inv = inventoryRepository.save(inv);

        // Apply new stock effect
        int currentStock = item.getStock();
        if (request.getType() == Type.T) {
            item.setStock(currentStock + request.getQuantity());
        } else {
            int newStock = currentStock - request.getQuantity();
            if (newStock < 0) {
                throw new com.lawencon.inventory.exception.InsufficientStockException(
                        "Insufficient stock for withdrawal. Item: " + item.getName() + ", available: " + currentStock + ", requested: " + request.getQuantity());
            }
            item.setStock(newStock);
        }
        itemService.updateStock(item);

        return toResponse(inv);
    }

    @Transactional
    public void delete(Long id) {
        Inventory inv = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", id));
        Item item = inv.getItem();
        // Revert stock
        if (inv.getType() == Type.T) {
            item.setStock(item.getStock() - inv.getQuantity());
        } else {
            item.setStock(item.getStock() + inv.getQuantity());
        }
        itemService.updateStock(item);
        inventoryRepository.deleteById(id);
    }

    private InventoryResponse toResponse(Inventory inv) {
        InventoryResponse r = new InventoryResponse();
        r.setId(inv.getId());
        r.setItemId(inv.getItem().getId());
        r.setItemName(inv.getItem().getName());
        r.setType(inv.getType());
        r.setQuantity(inv.getQuantity());
        return r;
    }
}
