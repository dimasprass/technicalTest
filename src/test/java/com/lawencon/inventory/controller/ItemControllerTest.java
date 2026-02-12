package com.lawencon.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawencon.inventory.dto.ItemRequest;
import com.lawencon.inventory.dto.ItemResponse;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void get_whenExists_returns200() throws Exception {
        ItemResponse response = new ItemResponse();
        response.setId(1L);
        response.setName("Item1");
        response.setPrice(BigDecimal.TEN);
        when(itemService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item1"))
                .andExpect(jsonPath("$.price").value(10));
    }

    @Test
    void get_whenNotExists_returns404() throws Exception {
        when(itemService.getById(999L)).thenThrow(new ResourceNotFoundException("Item not found with id: 999"));

        mockMvc.perform(get("/api/v1/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_validRequest_returns201() throws Exception {
        ItemRequest request = new ItemRequest();
        request.setName("NewItem");
        request.setPrice(BigDecimal.valueOf(99.99));
        ItemResponse response = new ItemResponse();
        response.setId(1L);
        response.setName("NewItem");
        response.setPrice(BigDecimal.valueOf(99.99));
        when(itemService.save(any(ItemRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("NewItem"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    void list_returnsPaginated() throws Exception {
        ItemResponse item = new ItemResponse();
        item.setId(1L);
        item.setName("Item1");
        item.setPrice(BigDecimal.ONE);
        when(itemService.findAll(any())).thenReturn(new PageImpl<>(List.of(item), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/items").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Item1"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
