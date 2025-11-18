package com.slimczes.orders.service.order.dto;

import com.slimczes.orders.domain.model.Money;

import java.util.List;
import java.util.UUID;

public record CreateOrderDto(
    UUID customerId,
    Money amount,
    List<OrderItem> items
) {

}
