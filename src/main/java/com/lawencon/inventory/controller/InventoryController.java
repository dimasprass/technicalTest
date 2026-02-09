package com.lawencon.inventory.controller;

import com.lawencon.inventory.dto.InventoryRequest;
import com.lawencon.inventory.dto.InventoryResponse;
import com.lawencon.inventory.dto.PageResponse;
import com.lawencon.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{id}")
    public InventoryResponse get(@PathVariable Long id) {
        return inventoryService.getById(id);
    }

    @GetMapping
    public PageResponse<InventoryResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return inventoryService.list(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse save(@Valid @RequestBody InventoryRequest request) {
        return inventoryService.save(request);
    }

    @PutMapping("/{id}")
    public InventoryResponse edit(@PathVariable Long id, @Valid @RequestBody InventoryRequest request) {
        return inventoryService.edit(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        inventoryService.delete(id);
    }
}
