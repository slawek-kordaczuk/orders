package com.slimczes.orders.adapter.messaging.in;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.slimczes.orders.adapter.messaging.event.ItemReservationFailed;
import com.slimczes.orders.adapter.messaging.event.ItemsReserved;
import com.slimczes.orders.adapter.messaging.mapper.OrderReservedMapper;
import com.slimczes.orders.service.order.OrderReservedService;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ItemReservedListener {

    private final OrderReservedService orderReservedService;
    private final OrderReservedMapper orderReservedMapper;

    @Incoming("reserved")
    public void handleItemsReserved(ItemsReserved event) {
        var orderReservedDto = orderReservedMapper.toOrderReservedFromEvent(event);
        orderReservedService.reserveOrder(orderReservedDto);
    }

    @Incoming("reserved-failed")
    public void handleItemReservationFailed(ItemReservationFailed event) {
        var orderReservedCancelDto = orderReservedMapper.toOrderReservedCancelFromEvent(event);
        orderReservedService.failedItemsOrder(orderReservedCancelDto);
    }
}
