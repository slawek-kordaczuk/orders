package com.slimczes.orders.domain.port.messaging;

import com.slimczes.orders.domain.event.PaymentCancelEvent;
import com.slimczes.orders.domain.event.PaymentCreateEvent;

public interface PaymentPublisher {

    void publishPayment(PaymentCreateEvent paymentCreateEvent);
    void publishPaymentCancel(PaymentCancelEvent paymentCancelEvent);
}
