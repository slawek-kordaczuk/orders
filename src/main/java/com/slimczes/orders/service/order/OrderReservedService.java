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
public class OrderReservedService {

    private final OrderRepository orderRepository;

    @Transactional
    public void reserveOrder(OrderReservedDto orderReservedDto) {
        orderRepository.findById(orderReservedDto.orderId()).ifPresentOrElse(o -> {
            orderReservedDto.reservedItems()
                    .forEach(item -> reservationItem(o, item));
            o.resolveOrderStatus(o.getVersion());
            orderRepository.upsert(o);
        }, () -> {
            throw new IllegalArgumentException("Order not found: " + orderReservedDto.orderId());
        });
    }

    @Transactional
    public void failedItemsOrder(OrderReservedFailedDto orderReservedFailedDto) {
        orderRepository.findById(orderReservedFailedDto.orderId()).ifPresentOrElse(o -> {
            orderReservedFailedDto.failedItems()
                    .forEach(reservedItem -> o.getItems().stream()
                            .filter(item -> item.getSku().equals(reservedItem.sku()))
                            .findAny()
                            .ifPresent(i -> resolveFailedStatus(i, reservedItem)));
            o.resolveOrderStatus(o.getVersion());
            orderRepository.upsert(o);
        }, () -> {
            throw new IllegalArgumentException("Order not found: " + orderReservedFailedDto.orderId());
        });
    }

    private static void reservationItem(Order o, OrderReservedItem reservedItem) {
        o.getItems().stream()
                .filter(item -> item.getSku().equals(reservedItem.sku()))
                .findAny()
                .ifPresent(i -> i.updateStatus(ItemStatus.RESERVED));
    }

    private static void resolveFailedStatus(OrderItem i, OrderReservedFailedItem cancelItem) {
        switch (cancelItem.reason()) {
            case NOT_FOUND -> i.updateStatus(ItemStatus.NOT_FOUND);
            case NOT_AVAILABLE -> i.updateStatus(ItemStatus.NOT_AVAILABLE);
            case NOT_ACTIVE -> i.updateStatus(ItemStatus.NOT_ACTIVE);
            case INVAlID_QUANTITY -> i.updateStatus(ItemStatus.INVAlID_QUANTITY);
        }
    }

}
