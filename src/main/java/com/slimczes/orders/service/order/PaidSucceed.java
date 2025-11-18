package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.model.PaymentStatus;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.order.dto.PaidSucceedDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PaidSucceed {

    private final OrderRepository orderRepository;

    public void paidOrder(PaidSucceedDto paidSucceedDto) {
        orderRepository.findById(paidSucceedDto.orderId()).ifPresentOrElse(
                order -> {
                    order.markPaymentCompleted();
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
