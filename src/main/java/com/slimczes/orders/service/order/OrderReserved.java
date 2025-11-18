package com.slimczes.orders.service.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.slimczes.orders.domain.model.ItemStatus;
import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.model.OrderItem;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.order.dto.OrderReservedDto;
import com.slimczes.orders.service.order.dto.OrderReservedFailedDto;
import com.slimczes.orders.service.order.dto.OrderReservedFailedItem;
import com.slimczes.orders.service.order.dto.OrderReservedItem;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class OrderReserved {

    private final OrderRepository orderRepository;

    @Transactional
    public void reserveOrder(OrderReservedDto orderReservedDto) {
        orderRepository.findById(orderReservedDto.orderId()).ifPresentOrElse(order -> {
            orderReservedDto.reservedItems()
                    .forEach(item -> reservationItem(order, item));
            order.markItemsReserved();
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
