package com.slimczes.orders.service.order.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.slimczes.orders.domain.model.OrderStatus;
import com.slimczes.orders.domain.model.PaymentStatus;

public record OrderResponseDto(
    UUID id,
    UUID customerId,
    OrderStatus orderStatus,
    PaymentStatus paymentStatus,
    List<OrderReservedItem> items,
    Instant createdAt,
    Instant updatedAt
) {

}
