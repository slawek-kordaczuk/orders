package com.slimczes.orders.domain.model;

import java.math.BigDecimal;

public record Money(BigDecimal amount, String currency) {
}
