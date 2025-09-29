package com.slimczes.orders.service.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;

import com.slimczes.orders.domain.event.OrderCancelled;
import com.slimczes.orders.domain.event.OrderCreated;
import com.slimczes.orders.domain.model.ItemStatus;
import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.model.OrderItem;
import com.slimczes.orders.domain.port.messaging.ItemReservationPublisher;
import com.slimczes.orders.domain.port.repository.CustomerRepository;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.order.dto.CancelOrderDto;
import com.slimczes.orders.service.order.dto.CreateOrderDto;
import com.slimczes.orders.service.order.dto.OrderResponseDto;
import com.slimczes.orders.service.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Slf4j
public class CreateOrderService {

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;

    private final ItemReservationPublisher itemReservationPublisher;

    private final OrderMapper orderMapper;

    public UUID createOrder(CreateOrderDto createOrderDto) {
        return customerRepository.findById(createOrderDto.customerId())
                                 .map(customer -> {
                                     Order order = new Order(customer.getId());
                                     createOrderDto.items()
                                                   .forEach(item -> order.createReservationItem(
                                                       item.sku(),
                                                       item.name(),
                                                       item.quantity()));
                                     OrderCreated orderCreated = orderMapper.toOrderCreated(order);
                                     itemReservationPublisher.requestItemReservation(orderCreated);
                                     return orderRepository.upsert(order).getId();
                                 }).orElseThrow(() -> new IllegalArgumentException("Customer not found: " + createOrderDto.customerId()));
    }

    public void cancelOrder(CancelOrderDto cancelOrderDto) {
        customerRepository.findById(cancelOrderDto.customerId())
                          .ifPresentOrElse(customer ->
                              orderRepository.findById(cancelOrderDto.orderId())
                                             .ifPresentOrElse(order -> {
                                                 order.getItems()
                                                      .forEach(item -> cancelItemAndPublishEvent(cancelOrderDto, order, item));
                                                 order.cancel();
                                                 orderRepository.upsert(order);
                                                 }, () -> {
                                                 throw new IllegalArgumentException("Order not found: " + cancelOrderDto.orderId());
                                             }), () -> {
                              throw new IllegalArgumentException("Customer not found: " + cancelOrderDto.customerId());
                          });
    }

    public OrderResponseDto getOrder(UUID orderId) {
        return orderRepository.findById(orderId).map(orderMapper::toOrderResponseDto).orElseGet(() -> {
            log.warn("Order not found: {}", orderId);
            return null;
        });
    }

    private void cancelItemAndPublishEvent(CancelOrderDto cancelOrderDto, Order order, OrderItem item) {
        orderRepository.findOrderItemBySku(item.getSku())
                       .ifPresentOrElse(orderItem -> orderItem.updateStatus(ItemStatus.CANCELED),
                           () -> log.warn("OrderItem not found: {}", item.getSku()));
        OrderCancelled orderCancelled = orderMapper.toOrderCancelled(order, cancelOrderDto.reason());
        itemReservationPublisher.requestItemReservationCancel(orderCancelled);
    }
}
