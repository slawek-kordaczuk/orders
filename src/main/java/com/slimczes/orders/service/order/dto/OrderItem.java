package com.slimczes.orders.service.order.dto;

public record OrderItem(
    String sku,
    String name,
    int quantity
) {

}
