package com.slimczes.orders.domain.model;

public enum PaymentStatus {
    PENDING,
    COMPLETED,
    CANCELLED,
    NOT_ENOUGH_FUNDS,
    WRONG_CURRENCY
}
