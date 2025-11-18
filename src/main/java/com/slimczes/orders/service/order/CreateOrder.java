package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.event.OrderCreateEvent;
import com.slimczes.orders.domain.event.PaymentCreateEvent;
import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.port.messaging.ItemReservationPublisher;
import com.slimczes.orders.domain.port.messaging.PaymentPublisher;
import com.slimczes.orders.domain.port.repository.CustomerRepository;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.order.dto.CreateOrderDto;
import com.slimczes.orders.service.order.dto.OrderResponseDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Slf4j
public class CreateOrder {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ItemReservationPublisher itemReservationPublisher;
    private final PaymentPublisher paymentPublisher;
    private final OrderMapper orderMapper;

    @Transactional
    public UUID createOrder(CreateOrderDto createOrderDto) {

        return customerRepository.findById(createOrderDto.customerId())
                .map(customer -> {
                    Order order = new Order(customer.getId());
                    createOrderDto.items()
                            .forEach(item -> order.createReservationItem(
                                    item.sku(),
                                    item.name(),
                                    item.quantity()));
                    UUID orderId = orderRepository.upsert(order).getId();
                    OrderCreateEvent orderCreateEvent = orderMapper.toOrderCreated(order, orderId);
                    PaymentCreateEvent paymentCreateEvent = orderMapper.toPaymentCreateEvent(orderId, customer.getId(), createOrderDto.amount());
                    itemReservationPublisher.publishItemReservation(orderCreateEvent);
                    paymentPublisher.publishPayment(paymentCreateEvent);
                    return orderId;
                }).orElseThrow(() -> new IllegalArgumentException("Customer not found: " + createOrderDto.customerId()));
    }

    public OrderResponseDto getOrder(UUID orderId) {
        return orderRepository.findById(orderId).map(orderMapper::toOrderResponseDto).orElseGet(() -> {
            log.warn("Order not found: {}", orderId);
            return null;
        });
    }
}
