package com.slimczes.orders.domain.model;

import java.util.Objects;

public record OrderItemSnapshot(
    String sku,
    String name,
    int quantity
) {
}
