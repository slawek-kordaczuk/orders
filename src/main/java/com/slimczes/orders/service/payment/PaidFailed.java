package com.slimczes.orders.service.payment;

import com.slimczes.orders.domain.model.PaymentStatus;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.payment.dto.PaidFailedDto;
import com.slimczes.orders.service.payment.dto.PaidFailedReasonDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Slf4j
public class PaidFailed {

    private final OrderRepository orderRepository;

    @Transactional
    public void paidFailed(PaidFailedDto paidFailedDto) {
        log.info("Paid Failed for orderId: {}", paidFailedDto.orderId());
        orderRepository.findById(paidFailedDto.orderId()).ifPresentOrElse(order -> {
            order.updatePaymentStatus(resolveFailedStatus(paidFailedDto.reason()));
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
