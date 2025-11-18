package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.model.ItemStatus;
import com.slimczes.orders.domain.model.OrderItem;
import com.slimczes.orders.domain.model.OrderStatus;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.order.dto.OrderReservedFailedDto;
import com.slimczes.orders.service.order.dto.OrderReservedFailedItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class OrderFailedReserved {

    private final OrderRepository orderRepository;

    @Transactional
    public void failedItemsOrder(OrderReservedFailedDto orderReservedFailedDto) {
        orderRepository.findById(orderReservedFailedDto.orderId()).ifPresentOrElse(order -> {
            orderReservedFailedDto.failedItems()
                    .forEach(reservedItem -> order.getItems().stream()
                            .filter(item -> item.getSku().equals(reservedItem.sku()))
                            .findAny()
                            .ifPresent(i -> resolveFailedStatus(i, reservedItem)));
            order.markItemsReserved();
            order.resolveOrderStatus();
            orderRepository.upsert(order);
        }, () -> {
            throw new IllegalArgumentException("Order not found: " + orderReservedFailedDto.orderId());
        });
    }

    private static void resolveFailedStatus(OrderItem item, OrderReservedFailedItem cancelItem) {
        switch (cancelItem.reason()) {
            case NOT_FOUND -> item.updateStatus(ItemStatus.NOT_FOUND);
            case NOT_AVAILABLE -> item.updateStatus(ItemStatus.NOT_AVAILABLE);
            case NOT_ACTIVE -> item.updateStatus(ItemStatus.NOT_ACTIVE);
            case INVAlID_QUANTITY -> item.updateStatus(ItemStatus.INVAlID_QUANTITY);
        }
    }
}
