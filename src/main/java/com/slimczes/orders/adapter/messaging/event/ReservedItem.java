package com.slimczes.orders.adapter.messaging.event;

import java.util.UUID;

public record ReservedItem(
    UUID itemId,
    String sku,
    String name,
    int reservedQuantity
) {

}
