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
) implements DomainEvent {

    public OrderCancelled(UUID orderId, UUID customerId, String reason, List<OrderItemSnapshot> items, Instant occurredAt) {
        this(UUID.randomUUID(), orderId, customerId, reason, items, occurredAt);
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getEventType() {
        return "OrderCancelled";
    }
}
