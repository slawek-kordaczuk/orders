package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.event.OrderCancelEvent;
import com.slimczes.orders.domain.event.PaymentCancelEvent;
import com.slimczes.orders.domain.port.messaging.ItemReservationPublisher;
import com.slimczes.orders.domain.port.messaging.PaymentPublisher;
import com.slimczes.orders.domain.port.repository.CustomerRepository;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.order.dto.CancelOrderDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Slf4j
public class CancelOrder {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ItemReservationPublisher itemReservationPublisher;
    private final PaymentPublisher paymentPublisher;
    private final OrderMapper orderMapper;

    @Transactional
    public void cancelOrder(CancelOrderDto cancelOrderDto) {
        customerRepository.findById(cancelOrderDto.customerId())
                .ifPresentOrElse(customer ->
                        orderRepository.findById(cancelOrderDto.orderId())
                                .ifPresentOrElse(order -> {
                                    order.cancel();
                                    OrderCancelEvent orderCancelEvent = orderMapper.toOrderCancelled(order, cancelOrderDto.reason());
                                    PaymentCancelEvent paymentCancelEvent = orderMapper.toPaymentCancelEvent(order.getId(), customer.getId());
                                    itemReservationPublisher.publishItemReservationCancel(orderCancelEvent);
                                    paymentPublisher.publishPaymentCancel(paymentCancelEvent);
                                    orderRepository.upsert(order);
                                }, () -> {
                                    throw new IllegalArgumentException("Order not found: " + cancelOrderDto.orderId());
                                }), () -> {
                    throw new IllegalArgumentException("Customer not found: " + cancelOrderDto.customerId());
                });
    }
}
