package com.slimczes.orders.adapter.messaging.event;

public record ItemFailed(
    String sku,
    ItemFailedReason reason,
    int requestedQuantity,
    int availableQuantity
) {

}
