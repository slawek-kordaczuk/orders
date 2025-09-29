package com.slimczes.orders.service.order.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.slimczes.orders.domain.model.OrderStatus;

public record OrderResponseDto(
    UUID id,
    UUID customerId,
    OrderStatus status,
    int version,
    List<OrderReservedItem> items,
    Instant createdAt,
    Instant updatedAt
) {

}
