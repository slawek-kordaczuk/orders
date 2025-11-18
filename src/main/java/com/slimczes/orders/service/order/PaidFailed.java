package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.model.PaymentStatus;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.order.dto.PaidFailedDto;
import com.slimczes.orders.service.order.dto.PaidFailedReasonDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PaidFailed {

    private final OrderRepository orderRepository;

    public void paidFailed(PaidFailedDto paidFailedDto) {
        orderRepository.findById(paidFailedDto.orderId()).ifPresentOrElse(order -> {
            order.updatePaymentStatus(resolveFailedStatus(paidFailedDto.reason()));
            order.markPaymentCompleted();
            order.resolveOrderStatus();
            orderRepository.upsert(order);
        }, () -> {
            throw new IllegalArgumentException("Order not found: " + paidFailedDto.orderId());
        });
    }

    private PaymentStatus resolveFailedStatus(PaidFailedReasonDto reason) {
        return switch (reason) {
            case WRONG_CURRENCY ->  PaymentStatus.WRONG_CURRENCY;
            case NOT_ENOUGH_FUNDS ->   PaymentStatus.NOT_ENOUGH_FUNDS;
        };
    }
}
