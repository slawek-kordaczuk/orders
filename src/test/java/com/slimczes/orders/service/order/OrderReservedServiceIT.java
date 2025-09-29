package com.slimczes.orders.service.order;

import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.slimczes.orders.domain.model.ItemStatus;
import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.model.OrderStatus;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.order.dto.OrderReservedDto;
import com.slimczes.orders.service.order.dto.OrderReservedItem;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class OrderReservedServiceIT {

    @Inject
    OrderReservedService orderReservedService;

    @Inject
    OrderRepository orderRepository;

    private Order testOrder;

    @BeforeEach
    @TestTransaction
    void setUp() {
    }

    @Test
    @TestTransaction
    @DisplayName("Should successfully reserve order items and set order to fulfilled")
    void shouldReserveOrderItemsAndSetOrderToFulfilled() {
        // Given - use the actual SKU from test data
        List<OrderReservedItem> reservedItems = List.of(
            new OrderReservedItem(UUID.randomUUID(), "TEST-SKU-001", "Test Product", 1)
        );
        OrderReservedDto orderReservedDto = new OrderReservedDto(
            UUID.randomUUID(),
            UUID.fromString("33333333-3333-3333-3333-333333333333"),
            reservedItems,
            Instant.now()
        );

        // When
        orderReservedService.reserveOrder(orderReservedDto);

        // Then
        Order updatedOrder = orderRepository.findById(UUID.fromString("33333333-3333-3333-3333-333333333333")).orElseThrow();
        assertEquals(OrderStatus.FULFILLED, updatedOrder.getStatus());
        assertEquals(1, updatedOrder.getVersion());

        updatedOrder.getItems().forEach(item -> {
            assertEquals(ItemStatus.RESERVED, item.getStatus());
        });
    }
}
