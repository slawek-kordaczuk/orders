package com.slimczes.orders.adapter.messaging.in;

import com.slimczes.orders.adapter.messaging.event.PaidEvent;
import com.slimczes.orders.adapter.messaging.event.PaidFailedEvent;
import com.slimczes.orders.service.order.PaidFailed;
import com.slimczes.orders.service.order.PaidSucceed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Slf4j
public class PaidListener {

    private final PaidSucceed paidSucceed;
    private final PaidFailed paidFailed;
    private final OrderReservedMapper orderReservedMapper;

    @Incoming("paid")
    public void handlePaid(PaidEvent event) {
        log.info("Received paid event {}", event);
        var paidSucceedDto = orderReservedMapper.toPaidSucceedFromEvent(event);
        paidSucceed.paidOrder(paidSucceedDto);
    }

    @Incoming("paid-failed")
    public void handlePaidFailed(PaidFailedEvent event) {
        log.info("Received paid failed event {}", event);
        var paidFailedDto = orderReservedMapper.toPaidFailedFromEvent(event);
        paidFailed.paidFailed(paidFailedDto);
    }

}
