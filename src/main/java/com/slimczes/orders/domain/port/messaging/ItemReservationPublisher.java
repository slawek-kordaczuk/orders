package com.slimczes.orders.domain.port.messaging;

import com.slimczes.orders.domain.event.OrderCancelled;
import com.slimczes.orders.domain.event.OrderCreated;

public interface ItemReservationPublisher {
    void requestItemReservation(OrderCreated orderCreated);
    void requestItemReservationCancel(OrderCancelled orderCancelled);
}
