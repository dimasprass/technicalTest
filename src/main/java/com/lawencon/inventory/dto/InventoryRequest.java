package com.lawencon.inventory.dto;

import com.lawencon.inventory.entity.Inventory.Type;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class InventoryRequest {

    @NotNull(message = "Item ID is mandatory")
    private Long itemId;

    @NotNull(message = "Type is mandatory (T for Top Up, W for Withdrawal)")
    private Type type;

    @NotNull(message = "Quantity is mandatory")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
