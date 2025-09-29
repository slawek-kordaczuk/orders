package com.slimczes.orders.service.order.dto;

import java.util.UUID;

public record OrderReservedItem(
    UUID itemId,
    String sku,
    String name,
    int reservedQuantity
) {

}
