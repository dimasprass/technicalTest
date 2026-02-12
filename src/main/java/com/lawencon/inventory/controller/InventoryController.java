package com.lawencon.inventory.controller;

import com.lawencon.inventory.dto.InventoryRequest;
import com.lawencon.inventory.dto.InventoryResponse;
import com.lawencon.inventory.dto.PageResponse;
import com.lawencon.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> save(@Valid @RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getById(@PathVariable Long id) {
        InventoryResponse response = inventoryService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<InventoryResponse>> list(
            @PageableDefault(size = 10) Pageable pageable) {
        PageResponse<InventoryResponse> response = PageResponse.of(inventoryService.findAll(pageable));
        return ResponseEntity.ok(response);
    }
}
