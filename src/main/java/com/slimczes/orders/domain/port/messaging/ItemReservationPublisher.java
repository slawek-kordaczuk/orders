package com.slimczes.orders.domain.port.messaging;

import com.slimczes.orders.domain.event.OrderCancelled;
import com.slimczes.orders.domain.event.OrderCreated;

public interface ItemReservationPublisher {
    void publishItemReservation(OrderCreated orderCreated);
    void publishItemReservationCancel(OrderCancelled orderCancelled);
}
