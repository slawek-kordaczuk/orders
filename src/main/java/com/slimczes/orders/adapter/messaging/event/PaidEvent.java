package com.slimczes.orders.adapter.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record PaidEvent(
        UUID eventId,
        UUID orderId,
        Instant occurredAt
) {
}
