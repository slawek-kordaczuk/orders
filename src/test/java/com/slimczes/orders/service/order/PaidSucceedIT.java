package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.model.OrderStatus;
import com.slimczes.orders.domain.model.PaymentStatus;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.order.dto.PaidSucceedDto;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class PaidSucceedIT {

    @Inject
    OrderRepository orderRepository;

    @Inject
    PaidSucceed paidSucceed;

    @Test
    @TestTransaction
    @DisplayName("Should mark payment as completed and set order to fulfilled")
    void shouldMarkPaymentAsCompletedAndSetOrderToFulfilled() {
        // Given
        UUID orderId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        PaidSucceedDto paidSucceedDto = new PaidSucceedDto(
                UUID.randomUUID(),
                orderId
        );

        // When
        paidSucceed.paidOrder(paidSucceedDto);

        // Then
        Order updatedOrder = orderRepository.findById(orderId).orElseThrow();
        assertEquals(PaymentStatus.COMPLETED, updatedOrder.getPaymentStatus());
        assertTrue(updatedOrder.getIsPaymentCompleted());
        assertEquals(OrderStatus.PENDING, updatedOrder.getOrderStatus());
    }

}
