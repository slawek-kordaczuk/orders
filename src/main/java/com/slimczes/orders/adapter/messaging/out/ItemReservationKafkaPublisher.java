package com.slimczes.orders.adapter.messaging.out;

import jakarta.enterprise.context.ApplicationScoped;

import com.slimczes.orders.domain.event.OrderCancelled;
import com.slimczes.orders.domain.event.OrderCreated;
import com.slimczes.orders.domain.port.messaging.ItemReservationPublisher;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
@Slf4j
class ItemReservationKafkaPublisher implements ItemReservationPublisher {

    @Channel("reservations")
    Emitter<OrderCreated> reservationEmitter;

    @Channel("reservations-cancelled")
    Emitter<OrderCancelled> cancellationEmitter;

    @Override
    public void publishItemReservation(OrderCreated orderCreated) {
        log.info("Publishing item reservation request for order: {}", orderCreated);
        reservationEmitter.send(orderCreated).whenComplete((v, ex) -> {
            if (ex != null) {
                log.error("Error sending item reservation request for order: {}", orderCreated.orderId(), ex);
            } else {
                log.info("Item reservation request sent for order: {}", orderCreated.orderId());
            }
        });
    }

    @Override
    public void publishItemReservationCancel(OrderCancelled orderCancelled) {
        log.info("Publishing item reservation cancellation for order: {}", orderCancelled);
        cancellationEmitter.send(orderCancelled).whenComplete((v, ex) -> {
            if (ex != null) {
                log.error("Error sending item reservation cancellation for order: {}", orderCancelled.orderId(), ex);
            } else {
                log.info("Item reservation cancellation sent for order: {}", orderCancelled.orderId());
            }
        });
    }
}
