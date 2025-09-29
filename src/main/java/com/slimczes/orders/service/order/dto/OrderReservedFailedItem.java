package com.slimczes.orders.service.order.dto;

public record OrderReservedFailedItem(
    String sku,
    OrderReservedFailedItemReason reason,
    int requestedQuantity,
    int availableQuantity
) {

}
