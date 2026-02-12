package com.lawencon.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawencon.inventory.dto.InventoryRequest;
import com.lawencon.inventory.dto.InventoryResponse;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    @Test
    void get_whenExists_returns200() throws Exception {
        InventoryResponse response = new InventoryResponse();
        response.setId(1L);
        response.setItemId(1L);
        response.setItemName("Item1");
        response.setQuantity(10);
        response.setType("IN");
        when(inventoryService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/inventories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.itemName").value("Item1"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.type").value("IN"));
    }

    @Test
    void get_whenNotExists_returns404() throws Exception {
        when(inventoryService.getById(999L)).thenThrow(new ResourceNotFoundException("Inventory not found with id: 999"));

        mockMvc.perform(get("/api/v1/inventories/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_validRequest_returns201() throws Exception {
        InventoryRequest request = new InventoryRequest();
        request.setItemId(1L);
        request.setQuantity(5);
        request.setType("IN");
        InventoryResponse response = new InventoryResponse();
        response.setId(1L);
        response.setItemId(1L);
        response.setItemName("Item1");
        response.setQuantity(5);
        response.setType("IN");
        when(inventoryService.save(any(InventoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.quantity").value(5))
                .andExpect(jsonPath("$.type").value("IN"));
    }

    @Test
    void list_returnsPaginated() throws Exception {
        InventoryResponse inv = new InventoryResponse();
        inv.setId(1L);
        inv.setItemId(1L);
        inv.setItemName("Item1");
        inv.setQuantity(10);
        inv.setType("IN");
        when(inventoryService.findAll(any())).thenReturn(new PageImpl<>(List.of(inv), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/inventories").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].itemName").value("Item1"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
