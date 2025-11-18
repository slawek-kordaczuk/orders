package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.model.Money;
import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.model.OrderStatus;
import com.slimczes.orders.domain.port.repository.OrderRepository;
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

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
public class CreateOrderIT {

    @Inject
    CreateOrder createOrder;

    @Inject
    OrderRepository orderRepository;

    @InjectKafkaCompanion
    KafkaCompanion kafkaCompanion;

    @Test
    @TestTransaction
    void shouldCreateOrderAndPublishReservationEvent() {
        // Given
        UUID consumerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Money amount = new Money(new BigDecimal("150.00"), "USD");
        CreateOrderDto createOrderDto = new CreateOrderDto(
                consumerId,
                amount,
                List.of(
                        new OrderItem("SKU001", "Test Item 1", 2),
                        new OrderItem("SKU002", "Test Item 2", 1)
                )
        );

        ConsumerTask<String, String> reservationConsumer = kafkaCompanion.consume(String.class)
                .fromTopics("test-item-reservation", 1);

        ConsumerTask<String, String> paymentConsumer = kafkaCompanion.consume(String.class)
                .fromTopics("test-payment", 1);

        // When
        UUID orderId = createOrder.createOrder(createOrderDto);

        // Then
        assertNotNull(orderId);
        Order savedOrder = orderRepository.findById(orderId).orElse(null);
        assertNotNull(savedOrder);
        assertEquals(consumerId, savedOrder.getCustomerId());
        assertEquals(OrderStatus.PENDING, savedOrder.getOrderStatus());
        assertEquals(2, savedOrder.getItems().size());
        savedOrder.getItems().forEach(item -> {
            assertNotNull(item.getId());
            assertTrue(List.of("SKU001", "SKU002").contains(item.getSku()));
        });
        List<ConsumerRecord<String, String>> reservationRecords = reservationConsumer.awaitCompletion(Duration.ofSeconds(10)).getRecords();
        assertEquals(1, reservationRecords.size());

        List<ConsumerRecord<String, String>> paymentRecords = paymentConsumer.awaitCompletion(Duration.ofSeconds(10)).getRecords();
        assertEquals(1, paymentRecords.size());

    }
}
