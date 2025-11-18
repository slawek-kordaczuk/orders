package com.slimczes.orders.domain.port.messaging;

import com.slimczes.orders.domain.event.OrderCancelEvent;
import com.slimczes.orders.domain.event.OrderCreateEvent;

public interface ItemReservationPublisher {
    void publishItemReservation(OrderCreateEvent orderCreateEvent);
    void publishItemReservationCancel(OrderCancelEvent orderCancelEvent);
}
