package com.slimczes.orders.service.order.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderReservedFailedDto(
    UUID eventId,
    UUID orderId,
    List<OrderReservedFailedItem> failedItems,
    Instant occurredAt
) {

}
