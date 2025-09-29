package com.slimczes.orders.adapter.messaging.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ItemsReserved(
    UUID eventId,
    UUID orderId,
    List<ReservedItem> reservedItems,
    Instant occurredAt
) {

}
