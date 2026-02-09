package com.lawencon.inventory.service;

import com.lawencon.inventory.dto.OrderRequest;
import com.lawencon.inventory.entity.Item;
import com.lawencon.inventory.entity.Order;
import com.lawencon.inventory.exception.InsufficientStockException;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void save_whenStockSufficient_createsOrderAndDeductsStock() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Widget");
        item.setStock(10);

        OrderRequest request = new OrderRequest();
        request.setItemId(1L);
        request.setQuantity(3);

        when(itemService.getEntityById(1L)).thenReturn(item);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setItem(item);
        savedOrder.setQuantity(3);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        var response = orderService.save(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(3, response.getQuantity());
        verify(itemService).updateStock(argThat(i -> i.getStock() == 7));
    }

    @Test
    void save_whenStockInsufficient_throwsInsufficientStockException() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Widget");
        item.setStock(2);

        OrderRequest request = new OrderRequest();
        request.setItemId(1L);
        request.setQuantity(5);

        when(itemService.getEntityById(1L)).thenReturn(item);

        assertThrows(InsufficientStockException.class, () -> orderService.save(request));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getById_whenNotExists_throwsResourceNotFoundException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.getById(999L));
    }
}
