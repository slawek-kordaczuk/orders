package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.model.ItemStatus;
import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.order.dto.OrderReservedFailedDto;
import com.slimczes.orders.service.order.dto.OrderReservedFailedItem;
import com.slimczes.orders.service.order.dto.OrderReservedFailedItemReason;
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
public class OrderFailedReservedIT {

    @Inject
    OrderFailedReserved orderFailedReserved;

    @Inject
    OrderRepository orderRepository;

    @Test
    @TestTransaction
    @DisplayName("Should handle failed item reservation and update order orderStatus")
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
        orderFailedReserved.failedItemsOrder(orderReservedFailedDto);

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
