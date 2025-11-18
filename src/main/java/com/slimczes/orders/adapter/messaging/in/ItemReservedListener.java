package com.slimczes.orders.adapter.messaging.in;

import com.slimczes.orders.service.order.OrderFailedReserved;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.slimczes.orders.adapter.messaging.event.ItemReservationFailed;
import com.slimczes.orders.adapter.messaging.event.ItemsReserved;
import com.slimczes.orders.service.order.OrderReserved;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Slf4j
public class ItemReservedListener {

    private final OrderReserved orderReserved;
    private final OrderFailedReserved orderFailedReserved;
    private final OrderReservedMapper orderReservedMapper;

    @Incoming("reserved")
    public void handleItemsReserved(ItemsReserved event) {
        log.info("Received reserved event {}", event);
        var orderReservedDto = orderReservedMapper.toOrderReservedFromEvent(event);
        orderReserved.reserveOrder(orderReservedDto);
    }

    @Incoming("reserved-failed")
    public void handleItemReservationFailed(ItemReservationFailed event) {
        log.info("Received reserved-failed event {}", event);
        var orderReservedCancelDto = orderReservedMapper.toOrderReservedCancelFromEvent(event);
        orderFailedReserved.failedItemsOrder(orderReservedCancelDto);
    }
}
