package com.slimczes.orders.domain.model;

import java.util.Objects;
import java.util.UUID;

public class OrderItem {
    private final UUID id;
    private final String sku;
    private final String itemName;
    private int quantity;
    private ItemStatus status;

    public OrderItem(String sku, String itemName, int quantity) {
        this.id = UUID.randomUUID();
        this.sku = sku;
        this.itemName = itemName;
        this.quantity = validateQuantity(quantity);
        this.status = ItemStatus.PENDING;
    }

    @Default
    public OrderItem(UUID id, String sku, String itemName, int quantity, ItemStatus status) {
        this.id = id;
        this.sku = sku;
        this.itemName = itemName;
        this.quantity = quantity;
        this.status = status;
    }

    public void updateQuantity(int newQuantity) {
        this.quantity = validateQuantity(newQuantity);
    }

    public void updateStatus(ItemStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus, "Status cannot be null");
    }

    private int validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        return quantity;
    }

    // Getters
    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public ItemStatus getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem orderItem)) return false;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
