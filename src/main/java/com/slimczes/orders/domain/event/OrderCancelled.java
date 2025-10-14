package com.slimczes.orders.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.slimczes.orders.domain.model.OrderItemSnapshot;

public record OrderCancelled(
        UUID eventId,
        UUID orderId,
        UUID customerId,
        String reason,
        List<OrderItemSnapshot> items,
        Instant occurredAt
) {
}
