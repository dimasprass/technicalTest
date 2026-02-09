package com.lawencon.inventory.service;

import com.lawencon.inventory.dto.InventoryRequest;
import com.lawencon.inventory.entity.Inventory;
import com.lawencon.inventory.entity.Item;
import com.lawencon.inventory.exception.InsufficientStockException;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.repository.InventoryRepository;
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
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void save_topUp_increasesItemStock() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setStock(10);

        InventoryRequest request = new InventoryRequest();
        request.setItemId(1L);
        request.setType(Inventory.Type.T);
        request.setQuantity(5);

        when(itemService.getEntityById(1L)).thenReturn(item);

        Inventory saved = new Inventory();
        saved.setId(1L);
        saved.setItem(item);
        saved.setType(Inventory.Type.T);
        saved.setQuantity(5);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(saved);

        var response = inventoryService.save(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(itemService).updateStock(argThat(i -> i.getStock() == 15));
    }

    @Test
    void save_withdrawal_whenSufficient_decreasesStock() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setStock(10);

        InventoryRequest request = new InventoryRequest();
        request.setItemId(1L);
        request.setType(Inventory.Type.W);
        request.setQuantity(3);

        when(itemService.getEntityById(1L)).thenReturn(item);

        Inventory saved = new Inventory();
        saved.setId(1L);
        saved.setItem(item);
        saved.setType(Inventory.Type.W);
        saved.setQuantity(3);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(saved);

        var response = inventoryService.save(request);

        assertNotNull(response);
        verify(itemService).updateStock(argThat(i -> i.getStock() == 7));
    }

    @Test
    void save_withdrawal_whenInsufficient_throwsInsufficientStockException() {
        Item item = new Item();
        item.setId(1L);
        item.setStock(2);

        InventoryRequest request = new InventoryRequest();
        request.setItemId(1L);
        request.setType(Inventory.Type.W);
        request.setQuantity(5);

        when(itemService.getEntityById(1L)).thenReturn(item);

        assertThrows(InsufficientStockException.class, () -> inventoryService.save(request));
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void getById_whenNotExists_throwsResourceNotFoundException() {
        when(inventoryRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getById(999L));
    }
}
