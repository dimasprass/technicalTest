package com.lawencon.inventory.service;

import com.lawencon.inventory.dto.ItemRequest;
import com.lawencon.inventory.dto.ItemResponse;
import com.lawencon.inventory.entity.Item;
import com.lawencon.inventory.exception.ResourceNotFoundException;
import com.lawencon.inventory.repository.ItemRepository;
import com.lawencon.inventory.service.impl.ItemServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void save_returnsResponse() {
        ItemRequest request = new ItemRequest();
        request.setName("Item1");
        request.setPrice(BigDecimal.TEN);
        Item saved = new Item();
        saved.setId(1L);
        saved.setName("Item1");
        saved.setPrice(BigDecimal.TEN);
        when(itemRepository.save(any(Item.class))).thenReturn(saved);

        ItemResponse response = itemService.save(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Item1");
        assertThat(response.getPrice()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void getById_whenExists_returnsResponse() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setPrice(BigDecimal.ONE);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemResponse response = itemService.getById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Item1");
    }

    @Test
    void getById_whenNotExists_throws() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void findAll_returnsPage() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setPrice(BigDecimal.ONE);
        when(itemRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(item), PageRequest.of(0, 10), 1));

        var page = itemService.findAll(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getName()).isEqualTo("Item1");
    }

    @Test
    void update_whenExists_returnsResponse() {
        Item existing = new Item();
        existing.setId(1L);
        existing.setName("Old");
        existing.setPrice(BigDecimal.ONE);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        ItemRequest request = new ItemRequest();
        request.setName("Updated");
        request.setPrice(BigDecimal.valueOf(20));
        ItemResponse response = itemService.update(1L, request);

        assertThat(response.getName()).isEqualTo("Updated");
        assertThat(response.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(20));
    }

    @Test
    void deleteById_whenExists_deletes() {
        when(itemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(itemRepository).deleteById(1L);

        itemService.deleteById(1L);

        verify(itemRepository).deleteById(1L);
    }
}
