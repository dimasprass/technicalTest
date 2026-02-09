package com.lawencon.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawencon.inventory.dto.ItemRequest;
import com.lawencon.inventory.dto.ItemResponse;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.exception.GlobalExceptionHandler;
import com.lawencon.inventory.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@Import(GlobalExceptionHandler.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void get_whenExists_returns200() throws Exception {
        ItemResponse response = new ItemResponse(1L, "Item", "Desc", 10);
        when(itemService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.remainingStock").value(10));
    }

    @Test
    void get_whenNotExists_returns404() throws Exception {
        when(itemService.getById(999L)).thenThrow(new ResourceNotFoundException("Item", 999L));

        mockMvc.perform(get("/api/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_validRequest_returns201() throws Exception {
        ItemRequest request = new ItemRequest();
        request.setName("New Item");
        request.setDescription("Desc");
        request.setStock(5);

        ItemResponse response = new ItemResponse(1L, "New Item", "Desc", 5);
        when(itemService.save(any(ItemRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Item"));
    }

    @Test
    void list_returnsPaginated() throws Exception {
        var page = new com.lawencon.inventory.dto.PageResponse<>(
                List.of(new ItemResponse(1L, "A", null, 1)),
                0, 10, 1, 1, true, true
        );
        when(itemService.list(any())).thenReturn(page);

        mockMvc.perform(get("/api/items?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
