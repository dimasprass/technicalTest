package com.lawencon.inventory.service.impl;

import com.lawencon.inventory.dto.OrderRequest;
import com.lawencon.inventory.dto.OrderResponse;
import com.lawencon.inventory.entity.Item;
import com.lawencon.inventory.entity.Order;
import com.lawencon.inventory.exception.InsufficientStockException;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.repository.InventoryRepository;
import com.lawencon.inventory.repository.ItemRepository;
import com.lawencon.inventory.repository.OrderRepository;
import com.lawencon.inventory.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ItemRepository itemRepository,
                            InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public OrderResponse save(OrderRequest request) {
        if (orderRepository.existsByOrderNo(request.getOrderNo())) {
            throw new IllegalArgumentException("Order number already exists: " + request.getOrderNo());
        }
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + request.getItemId()));

        int totalIn = inventoryRepository.findAll().stream()
                .filter(i -> i.getItem().getId().equals(item.getId()) && i.getType() == com.lawencon.inventory.entity.Inventory.Type.IN)
                .mapToInt(com.lawencon.inventory.entity.Inventory::getQuantity)
                .sum();
        int totalOut = inventoryRepository.findAll().stream()
                .filter(i -> i.getItem().getId().equals(item.getId()) && i.getType() == com.lawencon.inventory.entity.Inventory.Type.OUT)
                .mapToInt(com.lawencon.inventory.entity.Inventory::getQuantity)
                .sum();
        int available = totalIn - totalOut;
        if (available < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for item: " + item.getName() + ". Available: " + available);
        }

        Order order = new Order();
        order.setOrderNo(request.getOrderNo());
        order.setItem(item);
        order.setQuantity(request.getQuantity());
        order.setPrice(request.getPrice());
        order = orderRepository.save(order);
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setPrice(order.getPrice());
        response.setQuantity(order.getQuantity());
        response.setItemId(order.getItem().getId());
        response.setItemName(order.getItem().getName());
        response.setOrderDate(order.getOrderDate());
        return response;
    }
}
