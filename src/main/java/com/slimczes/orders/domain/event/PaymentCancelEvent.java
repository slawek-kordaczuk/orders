package com.slimczes.orders.domain.event;

import java.util.UUID;

public record PaymentCancelEvent(
        UUID clientId,
        UUID orderId
) {
}
