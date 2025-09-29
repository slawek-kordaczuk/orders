package com.slimczes.orders.service.order.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderReservedDto(
    UUID eventId,
    UUID orderId,
    List<OrderReservedItem> reservedItems,
    Instant occurredAt
) {
}
