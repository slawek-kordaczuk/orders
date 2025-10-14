package com.slimczes.orders.service.order.dto;

import java.util.List;
import java.util.UUID;

public record CreateOrderDto(
    UUID customerId,
    List<OrderItem> items
) {

}
