package com.slimczes.orders.service.payment.dto;

import java.util.UUID;

public record PaidFailedDto(
        UUID eventId,
        UUID orderId,
        PaidFailedReasonDto reason
) {
}
