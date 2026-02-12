package com.lawencon.inventory.service;

import com.lawencon.inventory.dto.InventoryRequest;
import com.lawencon.inventory.dto.InventoryResponse;
import com.lawencon.inventory.entity.Inventory;
import com.lawencon.inventory.entity.Item;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.repository.InventoryRepository;
import com.lawencon.inventory.repository.ItemRepository;
import com.lawencon.inventory.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @Test
    void save_returnsResponse() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Inventory saved = new Inventory();
        saved.setId(1L);
        saved.setItem(item);
        saved.setQuantity(10);
        saved.setType(Inventory.Type.IN);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(saved);

        InventoryRequest request = new InventoryRequest();
        request.setItemId(1L);
        request.setQuantity(10);
        request.setType("IN");
        InventoryResponse response = inventoryService.save(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getQuantity()).isEqualTo(10);
        assertThat(response.getType()).isEqualTo("IN");
    }

    @Test
    void getById_whenExists_returnsResponse() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        Inventory inv = new Inventory();
        inv.setId(1L);
        inv.setItem(item);
        inv.setQuantity(5);
        inv.setType(Inventory.Type.OUT);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inv));

        InventoryResponse response = inventoryService.getById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getItemName()).isEqualTo("Item1");
        assertThat(response.getQuantity()).isEqualTo(5);
    }

    @Test
    void getById_whenNotExists_throws() {
        when(inventoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void findAll_returnsPage() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        Inventory inv = new Inventory();
        inv.setId(1L);
        inv.setItem(item);
        inv.setQuantity(10);
        inv.setType(Inventory.Type.IN);
        when(inventoryRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(inv), PageRequest.of(0, 10), 1));

        var page = inventoryService.findAll(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getItemName()).isEqualTo("Item1");
    }
}
