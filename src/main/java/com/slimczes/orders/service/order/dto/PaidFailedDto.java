package com.slimczes.orders.service.order.dto;

import java.util.UUID;

public record PaidFailedDto(
        UUID eventId,
        UUID orderId,
        PaidFailedReasonDto reason
) {
}
