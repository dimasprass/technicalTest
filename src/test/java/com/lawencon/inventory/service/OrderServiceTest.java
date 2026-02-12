package com.lawencon.inventory.service;

import com.lawencon.inventory.dto.OrderRequest;
import com.lawencon.inventory.dto.OrderResponse;
import com.lawencon.inventory.entity.Inventory;
import com.lawencon.inventory.entity.Item;
import com.lawencon.inventory.entity.Order;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.repository.InventoryRepository;
import com.lawencon.inventory.repository.ItemRepository;
import com.lawencon.inventory.repository.OrderRepository;
import com.lawencon.inventory.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void save_whenEnoughStock_returnsResponse() {
        when(orderRepository.existsByOrderNo("ORD-001")).thenReturn(false);
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Inventory inStock = new Inventory();
        inStock.setItem(item);
        inStock.setQuantity(10);
        inStock.setType(Inventory.Type.IN);
        when(inventoryRepository.findAll()).thenReturn(List.of(inStock));

        Order saved = new Order();
        saved.setId(1L);
        saved.setOrderNo("ORD-001");
        saved.setItem(item);
        saved.setQuantity(2);
        saved.setPrice(BigDecimal.valueOf(100));
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        OrderRequest request = new OrderRequest();
        request.setOrderNo("ORD-001");
        request.setItemId(1L);
        request.setQuantity(2);
        request.setPrice(BigDecimal.valueOf(100));
        OrderResponse response = orderService.save(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOrderNo()).isEqualTo("ORD-001");
        assertThat(response.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    void getById_whenExists_returnsResponse() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        Order order = new Order();
        order.setId(1L);
        order.setOrderNo("ORD-001");
        order.setItem(item);
        order.setQuantity(1);
        order.setPrice(BigDecimal.TEN);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOrderNo()).isEqualTo("ORD-001");
        assertThat(response.getItemName()).isEqualTo("Item1");
    }

    @Test
    void getById_whenNotExists_throws() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void findAll_returnsPage() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        Order order = new Order();
        order.setId(1L);
        order.setOrderNo("ORD-001");
        order.setItem(item);
        order.setQuantity(1);
        order.setPrice(BigDecimal.TEN);
        when(orderRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1));

        var page = orderService.findAll(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getOrderNo()).isEqualTo("ORD-001");
    }
}
