package com.slimczes.orders.adapter.messaging.out;

import com.slimczes.orders.domain.event.PaymentCancelEvent;
import com.slimczes.orders.domain.event.PaymentCreateEvent;
import com.slimczes.orders.domain.port.messaging.PaymentPublisher;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
@Slf4j
public class PaymentKafkaPublisher implements PaymentPublisher {

    @Channel("payment")
    Emitter<PaymentCreateEvent> paymentEmitter;

    @Channel("payment-cancelled")
    Emitter<PaymentCancelEvent> paymentCancellEmitter;

    @Override
    public void publishPayment(PaymentCreateEvent paymentCreateEvent) {
        log.info("Publishing payment request for order: {}", paymentCreateEvent);
        paymentEmitter.send(paymentCreateEvent).whenComplete((v, ex) -> {
            if (ex != null) {
                log.error("Error sending payment request for order: {}", paymentCreateEvent.orderId(), ex);
            } else {
                log.info("Payment request sent for order: {}", paymentCreateEvent.orderId());
            }
        });
    }

    @Override
    public void publishPaymentCancel(PaymentCancelEvent paymentCancelEvent) {
        log.info("Publishing cancellation request for order: {}", paymentCancelEvent);
        paymentCancellEmitter.send(paymentCancelEvent).whenComplete((v, ex) -> {
            if (ex != null) {
                log.error("Error sending payment cancellation for order: {}", paymentCancelEvent.orderId(), ex);
            } else {
                log.info("Payment cancellation sent for order: {}", paymentCancelEvent.orderId());
            }
        });
    }
}
