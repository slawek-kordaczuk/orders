package com.slimczes.orders.domain.model;

import java.util.Objects;

public record OrderItemSnapshot(
    String sku,
    String name,
    int quantity
) {
    public OrderItemSnapshot {
        Objects.requireNonNull(sku, "Product SKU cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }
}
