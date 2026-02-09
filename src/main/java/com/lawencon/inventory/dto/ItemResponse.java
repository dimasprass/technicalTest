package com.lawencon.inventory.dto;

public class ItemResponse {
    private Long id;
    private String name;
    private String description;
    private Integer remainingStock;

    public ItemResponse() {
    }

    public ItemResponse(Long id, String name, String description, Integer remainingStock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.remainingStock = remainingStock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRemainingStock() {
        return remainingStock;
    }

    public void setRemainingStock(Integer remainingStock) {
        this.remainingStock = remainingStock;
    }
}
