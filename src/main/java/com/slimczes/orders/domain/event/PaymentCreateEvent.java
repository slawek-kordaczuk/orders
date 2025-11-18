package com.slimczes.orders.domain.event;

import com.slimczes.orders.domain.model.Money;

import java.util.UUID;

public record PaymentCreateEvent(
        UUID clientId,
        UUID orderId,
        Money amount
) {
}
