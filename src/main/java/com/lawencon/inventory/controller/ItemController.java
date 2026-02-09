package com.lawencon.inventory.controller;

import com.lawencon.inventory.dto.ItemRequest;
import com.lawencon.inventory.dto.ItemResponse;
import com.lawencon.inventory.dto.PageResponse;
import com.lawencon.inventory.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable Long id) {
        return itemService.getById(id);
    }

    @GetMapping
    public PageResponse<ItemResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return itemService.list(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse save(@Valid @RequestBody ItemRequest request) {
        return itemService.save(request);
    }

    @PutMapping("/{id}")
    public ItemResponse edit(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        return itemService.edit(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}
