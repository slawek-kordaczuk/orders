package com.slimczes.orders.service.payment;

import com.slimczes.orders.domain.model.PaymentStatus;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.payment.dto.PaidSucceedDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Slf4j
public class PaidSucceed {

    private final OrderRepository orderRepository;

    @Transactional
    public void paidOrder(PaidSucceedDto paidSucceedDto) {
        log.info("Paid Succeed for orderId: {}", paidSucceedDto.orderId());
        orderRepository.findById(paidSucceedDto.orderId()).ifPresentOrElse(
                order -> {
                    order.updatePaymentStatus(PaymentStatus.COMPLETED);
                    order.resolveOrderStatus();
                    orderRepository.upsert(order);
                },
                () -> {
                    throw new IllegalArgumentException("Order not found: " + paidSucceedDto.orderId());
                }
        );

    }

}
