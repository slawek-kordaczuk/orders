package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.model.ItemStatus;
import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.model.OrderStatus;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.order.dto.*;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class OrderReservedServiceIT {

    @Inject
    OrderReservedService orderReservedService;

    @Inject
    OrderRepository orderRepository;

    @Test
    @TestTransaction
    @DisplayName("Should successfully reserve order items and set order to fulfilled")
    void shouldReserveOrderItemsAndSetOrderToFulfilled() {
        UUID orderId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        List<OrderReservedItem> reservedItems = List.of(
                new OrderReservedItem(UUID.randomUUID(), "TEST-SKU-001", "Test Product 1", 1),
                new OrderReservedItem(UUID.randomUUID(), "TEST-SKU-002", "Test Product 2", 2)
        );
        OrderReservedDto orderReservedDto = new OrderReservedDto(
                UUID.randomUUID(),
                orderId,
                reservedItems,
                Instant.now()
        );

        // When
        orderReservedService.reserveOrder(orderReservedDto);

        // Then
        Order updatedOrder = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderStatus.FULFILLED, updatedOrder.getStatus());
        assertEquals(1, updatedOrder.getVersion());

        updatedOrder.getItems().forEach(item -> {
            assertEquals(ItemStatus.RESERVED, item.getStatus());
        });
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle failed item reservation and update order status")
    void shouldHandleFailedItemReservationAndUpdateOrderStatus() {
        // Given
        UUID orderId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        List<OrderReservedFailedItem> failedItems = List.of(
                new OrderReservedFailedItem("TEST-SKU-001", OrderReservedFailedItemReason.NOT_AVAILABLE, 50, 20),
                new OrderReservedFailedItem("TEST-SKU-002", OrderReservedFailedItemReason.NOT_FOUND, 70, 0)
        );
        OrderReservedFailedDto orderReservedFailedDto = new OrderReservedFailedDto(
                UUID.randomUUID(),
                orderId,
                failedItems,
                Instant.now()
        );

        // When
        orderReservedService.failedItemsOrder(orderReservedFailedDto);

        // Then
        Order updatedOrder = orderRepository.findById(orderId).orElse(null);
        assertNotNull(updatedOrder);

        updatedOrder.getItems().forEach(item -> {
            if (item.getSku().equals("TEST-SKU-001")) {
                assertEquals(ItemStatus.NOT_AVAILABLE, item.getStatus());
            }
            if (item.getSku().equals("TEST-SKU-002")) {
                assertEquals(ItemStatus.NOT_FOUND, item.getStatus());
            }
        });
    }
}
