package com.slimczes.orders.service.order.dto;

import java.util.UUID;

public record PaidSucceedDto(
        UUID eventId,
        UUID orderId
) {
}
