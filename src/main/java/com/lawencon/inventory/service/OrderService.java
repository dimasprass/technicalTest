package com.lawencon.inventory.service;

import com.lawencon.inventory.dto.OrderRequest;
import com.lawencon.inventory.dto.OrderResponse;
import com.lawencon.inventory.dto.PageResponse;
import com.lawencon.inventory.entity.Item;
import com.lawencon.inventory.entity.Order;
import com.lawencon.inventory.exception.InsufficientStockException;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemService itemService;

    public OrderService(OrderRepository orderRepository, ItemService itemService) {
        this.orderRepository = orderRepository;
        this.itemService = itemService;
    }

    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return toResponse(order);
    }

    public PageResponse<OrderResponse> list(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
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
    public OrderResponse save(OrderRequest request) {
        Item item = itemService.getEntityById(request.getItemId());
        int remainingStock = item.getStock();
        if (remainingStock < request.getQuantity()) {
            throw new InsufficientStockException(item.getName(), request.getQuantity(), remainingStock);
        }

        Order order = new Order();
        order.setItem(item);
        order.setQuantity(request.getQuantity());
        order = orderRepository.save(order);

        item.setStock(remainingStock - request.getQuantity());
        itemService.updateStock(item);

        return toResponse(order);
    }

    @Transactional
    public OrderResponse edit(Long id, OrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        Item item = itemService.getEntityById(request.getItemId());

        // Revert previous order quantity from stock
        Item prevItem = order.getItem();
        prevItem.setStock(prevItem.getStock() + order.getQuantity());
        itemService.updateStock(prevItem);

        int available = item.getStock();
        if (available < request.getQuantity()) {
            throw new InsufficientStockException(item.getName(), request.getQuantity(), available);
        }

        order.setItem(item);
        order.setQuantity(request.getQuantity());
        order = orderRepository.save(order);

        item.setStock(available - request.getQuantity());
        itemService.updateStock(item);

        return toResponse(order);
    }

    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        Item item = order.getItem();
        item.setStock(item.getStock() + order.getQuantity());
        itemService.updateStock(item);
        orderRepository.deleteById(id);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse r = new OrderResponse();
        r.setId(order.getId());
        r.setItemId(order.getItem().getId());
        r.setItemName(order.getItem().getName());
        r.setQuantity(order.getQuantity());
        return r;
    }
}
