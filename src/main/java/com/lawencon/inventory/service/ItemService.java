package com.lawencon.inventory.service;

import com.lawencon.inventory.dto.ItemRequest;
import com.lawencon.inventory.dto.ItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {

    ItemResponse save(ItemRequest request);

    ItemResponse getById(Long id);

    Page<ItemResponse> findAll(Pageable pageable);

    ItemResponse update(Long id, ItemRequest request);

    void deleteById(Long id);
}
