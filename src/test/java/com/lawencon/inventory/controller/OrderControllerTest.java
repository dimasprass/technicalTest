package com.lawencon.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawencon.inventory.dto.OrderRequest;
import com.lawencon.inventory.dto.OrderResponse;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    void get_whenExists_returns200() throws Exception {
        OrderResponse response = new OrderResponse();
        response.setId(1L);
        response.setOrderNo("ORD-001");
        response.setPrice(BigDecimal.valueOf(100));
        response.setQuantity(2);
        response.setItemId(1L);
        response.setItemName("Item1");
        response.setOrderDate(LocalDateTime.now());
        when(orderService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNo").value("ORD-001"))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.itemName").value("Item1"));
    }

    @Test
    void get_whenNotExists_returns404() throws Exception {
        when(orderService.getById(999L)).thenThrow(new ResourceNotFoundException("Order not found with id: 999"));

        mockMvc.perform(get("/api/v1/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_validRequest_returns201() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setOrderNo("ORD-002");
        request.setItemId(1L);
        request.setQuantity(3);
        request.setPrice(BigDecimal.valueOf(150));
        OrderResponse response = new OrderResponse();
        response.setId(1L);
        response.setOrderNo("ORD-002");
        response.setPrice(BigDecimal.valueOf(150));
        response.setQuantity(3);
        response.setItemId(1L);
        response.setItemName("Item1");
        response.setOrderDate(LocalDateTime.now());
        when(orderService.save(any(OrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNo").value("ORD-002"))
                .andExpect(jsonPath("$.price").value(150))
                .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    void list_returnsPaginated() throws Exception {
        OrderResponse order = new OrderResponse();
        order.setId(1L);
        order.setOrderNo("ORD-001");
        order.setPrice(BigDecimal.TEN);
        order.setQuantity(1);
        order.setItemId(1L);
        order.setItemName("Item1");
        order.setOrderDate(LocalDateTime.now());
        when(orderService.findAll(any())).thenReturn(new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/orders").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].orderNo").value("ORD-001"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
