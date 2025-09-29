package com.slimczes.orders.service.order.dto;

public record CreateOrderItem(
    String sku,
    String name,
    int quantity
) {

}
