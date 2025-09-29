package com.slimczes.orders.adapter.messaging.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ItemReservationFailed(
    UUID eventId,
    UUID orderId,
    List<ItemFailed> itemFailed,
    Instant occurredAt
) {

}
