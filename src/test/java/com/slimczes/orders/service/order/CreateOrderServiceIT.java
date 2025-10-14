package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.model.ItemStatus;
import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.model.OrderStatus;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import com.slimczes.orders.service.order.dto.CancelOrderDto;
import com.slimczes.orders.service.order.dto.CreateOrderDto;
import com.slimczes.orders.service.order.dto.OrderItem;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.ConsumerTask;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import jakarta.inject.Inject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
public class CreateOrderServiceIT {

    @Inject
    CreateOrderService createOrderService;

    @Inject
    OrderRepository orderRepository;

    @InjectKafkaCompanion
    KafkaCompanion kafkaCompanion;

    @Test
    @TestTransaction
    void shouldCreateOrderAndPublishReservationEvent() {
        // Given
        UUID consumerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        CreateOrderDto createOrderDto = new CreateOrderDto(
                consumerId,
                List.of(
                        new OrderItem("SKU001", "Test Item 1", 2),
                        new OrderItem("SKU002", "Test Item 2", 1)
                )
        );

        ConsumerTask<String, String> reservationConsumer = kafkaCompanion.consume(String.class).fromTopics("test-item-reservations", 1);

        // When
        UUID orderId = createOrderService.createOrder(createOrderDto);

        // Then
        assertNotNull(orderId);
        Order savedOrder = orderRepository.findById(orderId).orElse(null);
        assertNotNull(savedOrder);
        assertEquals(consumerId, savedOrder.getCustomerId());
        assertEquals(OrderStatus.NEW, savedOrder.getStatus());
        assertEquals(2, savedOrder.getItems().size());
        savedOrder.getItems().forEach(item -> {
            assertNotNull(item.getId());
            assertTrue(List.of("SKU001", "SKU002").contains(item.getSku()));
        });
        List<ConsumerRecord<String, String>> records = reservationConsumer.awaitCompletion(Duration.ofSeconds(10)).getRecords();
        assertEquals(1, records.size());

    }

    @Test
    @TestTransaction
    void shouldCancelOrderAndPublishCancellationEvent() {
        // Given
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        List<OrderItem> orderItems = List.of(
                new OrderItem("SKU001", "Test Item 1", 2),
                new OrderItem("SKU002", "Test Item 2", 1));
        CreateOrderDto createOrderDto = new CreateOrderDto(
                customerId,
                orderItems
        );
        UUID orderId = createOrderService.createOrder(createOrderDto);

        CancelOrderDto cancelOrderDto = new CancelOrderDto(
                orderId,
                customerId,
                "Customer requested cancellation"
        );
        ConsumerTask<String, String> cancellationConsumer = kafkaCompanion
                .consume(String.class)
                .fromTopics("test-item-reservations-cancelled", 1);

        // When
        createOrderService.cancelOrder(cancelOrderDto);

        // Then
        Order cancelledOrder = orderRepository.findById(orderId).orElse(null);
        assertNotNull(cancelledOrder);
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
        cancelledOrder.getItems().forEach(item ->
                assertEquals(ItemStatus.CANCELED, item.getStatus())
        );
        List<ConsumerRecord<String, String>> records = cancellationConsumer
                .awaitCompletion(Duration.ofSeconds(10))
                .getRecords();
        assertEquals(1, records.size());
    }
}
