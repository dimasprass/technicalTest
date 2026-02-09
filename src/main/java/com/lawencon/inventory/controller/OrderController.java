package com.lawencon.inventory.controller;

import com.lawencon.inventory.dto.OrderRequest;
import com.lawencon.inventory.dto.OrderResponse;
import com.lawencon.inventory.dto.PageResponse;
import com.lawencon.inventory.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping
    public PageResponse<OrderResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return orderService.list(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse save(@Valid @RequestBody OrderRequest request) {
        return orderService.save(request);
    }

    @PutMapping("/{id}")
    public OrderResponse edit(@PathVariable Long id, @Valid @RequestBody OrderRequest request) {
        return orderService.edit(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}
