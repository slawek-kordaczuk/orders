package com.slimczes.orders.service.order.dto;

import java.util.List;
import java.util.UUID;

public record CancelOrderDto(
    UUID orderId,
    UUID customerId,
    String reason
) {

}
