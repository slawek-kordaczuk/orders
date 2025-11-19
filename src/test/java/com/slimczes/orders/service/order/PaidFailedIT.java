package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.model.OrderStatus;
import com.slimczes.orders.domain.model.PaymentStatus;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.order.dto.PaidFailedDto;
import com.slimczes.orders.service.order.dto.PaidFailedReasonDto;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class PaidFailedIT {

    @Inject
    OrderRepository orderRepository;

    @Inject
    PaidFailed paidFailed;

    @Test
    @TestTransaction
    @DisplayName("Should mark payment as failed with NOT_ENOUGH_FUNDS status")
    void shouldMarkPaymentAsFailedWithNotEnoughFunds() {
        // Given
        UUID orderId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        PaidFailedDto paidFailedDto = new PaidFailedDto(
                UUID.randomUUID(),
                orderId,
                PaidFailedReasonDto.NOT_ENOUGH_FUNDS
        );

        // When
        paidFailed.paidFailed(paidFailedDto);

        // Then
        Order updatedOrder = orderRepository.findById(orderId).orElseThrow();
        assertEquals(PaymentStatus.NOT_ENOUGH_FUNDS, updatedOrder.getPaymentStatus());
        assertEquals(OrderStatus.PENDING, updatedOrder.getOrderStatus());
    }

}
