package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.event.OrderCancelEvent;
import com.slimczes.orders.domain.event.OrderCreateEvent;
import com.slimczes.orders.domain.event.PaymentCancelEvent;
import com.slimczes.orders.domain.model.Order;
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

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
                                    publishItemReservationCancel(order, cancelOrderDto.reason());
                                    publishPaymentCancel(order.getId(), customer.getId());
                                    orderRepository.upsert(order);
                                }, () -> {
                                    throw new IllegalArgumentException("Order not found: " + cancelOrderDto.orderId());
                                }), () -> {
                    throw new IllegalArgumentException("Customer not found: " + cancelOrderDto.customerId());
                });
    }

    private void publishItemReservationCancel(Order order, String reason) {
        CompletableFuture.supplyAsync(() -> orderMapper.toOrderCancelled(order, reason))
                .thenCompose(itemReservationPublisher::publishItemReservationCancel);
    }

    private void publishPaymentCancel(UUID orderId, UUID customerId) {
        CompletableFuture.supplyAsync(() -> orderMapper.toPaymentCancelEvent(orderId, customerId))
                .thenCompose(paymentPublisher::publishPaymentCancel);
    }
}
