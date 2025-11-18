package com.slimczes.orders.adapter.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record PaidFailedEvent(
        UUID eventId,
        UUID orderId,
        PaidFailedReason reason,
        Instant occurredAt
) {
}
