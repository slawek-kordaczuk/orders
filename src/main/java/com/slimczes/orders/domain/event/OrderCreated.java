package com.slimczes.orders.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.slimczes.orders.domain.model.OrderItemSnapshot;

public record OrderCreated(
        UUID eventId,
        UUID orderId,
        UUID customerId,
        List<OrderItemSnapshot> items,
        Instant occurredAt
) {
}
