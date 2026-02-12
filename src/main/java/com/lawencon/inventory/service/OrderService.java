package com.lawencon.inventory.service;

import com.lawencon.inventory.dto.OrderRequest;
import com.lawencon.inventory.dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse save(OrderRequest request);

    OrderResponse getById(Long id);

    Page<OrderResponse> findAll(Pageable pageable);
}
