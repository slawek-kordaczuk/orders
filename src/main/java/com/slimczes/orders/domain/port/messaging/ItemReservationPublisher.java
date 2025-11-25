package com.slimczes.orders.domain.port.messaging;

import com.slimczes.orders.domain.event.OrderCancelEvent;
import com.slimczes.orders.domain.event.OrderCreateEvent;

import java.util.concurrent.CompletionStage;

public interface ItemReservationPublisher {
    CompletionStage<Void> publishItemReservation(OrderCreateEvent orderCreateEvent);
    CompletionStage<Void> publishItemReservationCancel(OrderCancelEvent orderCancelEvent);
}
