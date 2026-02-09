package com.lawencon.inventory.service;

import com.lawencon.inventory.dto.ItemRequest;
import com.lawencon.inventory.dto.ItemResponse;
import com.lawencon.inventory.entity.Item;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void getById_whenExists_returnsResponse() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Desc");
        item.setStock(10);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemResponse response = itemService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Item", response.getName());
        assertEquals(10, response.getRemainingStock());
    }

    @Test
    void getById_whenNotExists_throwsResourceNotFoundException() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.getById(999L));
    }

    @Test
    void save_persistsItem() {
        ItemRequest request = new ItemRequest();
        request.setName("New Item");
        request.setDescription("Description");
        request.setStock(5);

        Item saved = new Item();
        saved.setId(1L);
        saved.setName(request.getName());
        saved.setDescription(request.getDescription());
        saved.setStock(request.getStock());
        when(itemRepository.save(any(Item.class))).thenReturn(saved);

        ItemResponse response = itemService.save(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("New Item", response.getName());
        assertEquals(5, response.getRemainingStock());
    }

    @Test
    void list_returnsPaginatedResponse() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setStock(1);
        Page<Item> page = new PageImpl<>(List.of(item));
        when(itemRepository.findAll(any(Pageable.class))).thenReturn(page);

        var result = itemService.list(org.springframework.data.domain.PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
    }

    @Test
    void delete_whenExists_deletes() {
        when(itemRepository.existsById(1L)).thenReturn(true);
        itemService.delete(1L);
        verify(itemRepository).deleteById(1L);
    }

    @Test
    void delete_whenNotExists_throws() {
        when(itemRepository.existsById(999L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> itemService.delete(999L));
    }
}
