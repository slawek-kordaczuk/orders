package com.slimczes.orders.domain.port.messaging;

import com.slimczes.orders.domain.event.PaymentCancelEvent;
import com.slimczes.orders.domain.event.PaymentCreateEvent;

import java.util.concurrent.CompletionStage;

public interface PaymentPublisher {

    CompletionStage<Void> publishPayment(PaymentCreateEvent paymentCreateEvent);
    CompletionStage<Void> publishPaymentCancel(PaymentCancelEvent paymentCancelEvent);
}
