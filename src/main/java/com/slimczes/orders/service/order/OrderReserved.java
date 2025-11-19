package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.model.ItemStatus;
import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.order.dto.OrderReservedDto;
import com.slimczes.orders.service.order.dto.OrderReservedItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Slf4j
public class OrderReserved {

    private final OrderRepository orderRepository;

    @Transactional
    public void reserveOrder(OrderReservedDto orderReservedDto) {
        log.info("Order Reserved for orderId: {}", orderReservedDto.orderId());
        orderRepository.findById(orderReservedDto.orderId()).ifPresentOrElse(order -> {
            orderReservedDto.reservedItems()
                    .forEach(item -> reservationItem(order, item));
            order.resolveOrderStatus();
            orderRepository.upsert(order);
        }, () -> {
            throw new IllegalArgumentException("Order not found: " + orderReservedDto.orderId());
        });
    }

    private static void reservationItem(Order order, OrderReservedItem reservedItem) {
        order.getItems().stream()
                .filter(item -> item.getSku().equals(reservedItem.sku()))
                .findAny()
                .ifPresent(i -> i.updateStatus(ItemStatus.RESERVED));
    }

}
