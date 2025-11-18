package com.slimczes.orders.adapter.messaging.out;

import com.slimczes.orders.domain.event.OrderCancelEvent;
import com.slimczes.orders.domain.event.OrderCreateEvent;
import jakarta.enterprise.context.ApplicationScoped;

import com.slimczes.orders.domain.port.messaging.ItemReservationPublisher;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
@Slf4j
class ItemReservationKafkaPublisher implements ItemReservationPublisher {

    @Channel("reservations")
    Emitter<OrderCreateEvent> reservationEmitter;

    @Channel("reservations-cancelled")
    Emitter<OrderCancelEvent> cancellationEmitter;

    @Override
    public void publishItemReservation(OrderCreateEvent orderCreateEvent) {
        log.info("Publishing item reservation request for order: {}", orderCreateEvent);
        reservationEmitter.send(orderCreateEvent).whenComplete((v, ex) -> {
            if (ex != null) {
                log.error("Error sending item reservation request for order: {}", orderCreateEvent.orderId(), ex);
            } else {
                log.info("Item reservation request sent for order: {}", orderCreateEvent.orderId());
            }
        });
    }

    @Override
    public void publishItemReservationCancel(OrderCancelEvent orderCancelEvent) {
        log.info("Publishing item reservation cancellation for order: {}", orderCancelEvent);
        cancellationEmitter.send(orderCancelEvent).whenComplete((v, ex) -> {
            if (ex != null) {
                log.error("Error sending item reservation cancellation for order: {}", orderCancelEvent.orderId(), ex);
            } else {
                log.info("Item reservation cancellation sent for order: {}", orderCancelEvent.orderId());
            }
        });
    }
}
