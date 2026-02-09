package com.lawencon.inventory.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String itemName, int requested, int available) {
        super("Insufficient stock. Item: " + itemName + ", requested: " + requested + ", available: " + available);
    }
}
