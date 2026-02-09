package com.lawencon.inventory.service;

import com.lawencon.inventory.dto.ItemRequest;
import com.lawencon.inventory.dto.ItemResponse;
import com.lawencon.inventory.dto.PageResponse;
import com.lawencon.inventory.entity.Item;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public ItemResponse getById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
        return toResponse(item);
    }

    public PageResponse<ItemResponse> list(Pageable pageable) {
        Page<Item> page = itemRepository.findAll(pageable);
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
    public ItemResponse save(ItemRequest request) {
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setStock(request.getStock() != null ? request.getStock() : 0);
        item = itemRepository.save(item);
        return toResponse(item);
    }

    @Transactional
    public ItemResponse edit(Long id, ItemRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        // Optionally allow updating stock directly for admin; spec says "remaining stock" from inventory/orders.
        // Here we keep stock as managed by inventory/orders; edit only name/description unless you want to allow stock override.
        item = itemRepository.save(item);
        return toResponse(item);
    }

    @Transactional
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item", id);
        }
        itemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Item getEntityById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
    }

    @Transactional
    public void updateStock(Item item) {
        itemRepository.save(item);
    }

    private ItemResponse toResponse(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getStock()
        );
    }
}
