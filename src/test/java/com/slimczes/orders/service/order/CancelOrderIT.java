package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.model.*;
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

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
public class CancelOrderIT {

    @Inject
    CreateOrder createOrder;

    @Inject
    CancelOrder cancelOrder;

    @Inject
    OrderRepository orderRepository;

    @InjectKafkaCompanion
    KafkaCompanion kafkaCompanion;

    @Test
    @TestTransaction
    void shouldCancelOrderAndPublishCancellationEvent() {
        // Given
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Money amount = new Money(new BigDecimal("150.00"), "USD");
        List<OrderItem> orderItems = List.of(
                new OrderItem("SKU001", "Test Item 1", 2),
                new OrderItem("SKU002", "Test Item 2", 1));
        CreateOrderDto createOrderDto = new CreateOrderDto(
                customerId,
                amount,
                orderItems
        );
        UUID orderId = createOrder.createOrder(createOrderDto);

        CancelOrderDto cancelOrderDto = new CancelOrderDto(
                orderId,
                customerId,
                "Customer requested cancellation"
        );
        ConsumerTask<String, String> orderCancelConsumer = kafkaCompanion
                .consume(String.class)
                .fromTopics("test-item-reservation-cancelled", 1);

        ConsumerTask<String, String> paymentCancelConsumer = kafkaCompanion
                .consume(String.class)
                .fromTopics("test-payment-cancelled", 1);

        // When
        cancelOrder.cancelOrder(cancelOrderDto);

        // Then
        Order cancelledOrder = orderRepository.findById(orderId).orElse(null);
        assertNotNull(cancelledOrder);
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getOrderStatus());
        assertEquals(PaymentStatus.CANCELLED, cancelledOrder.getPaymentStatus());
        cancelledOrder.getItems().forEach(item ->
                assertEquals(ItemStatus.CANCELED, item.getStatus())
        );
        List<ConsumerRecord<String, String>> orderCancelRecord = orderCancelConsumer
                .awaitCompletion(Duration.ofSeconds(10))
                .getRecords();
        assertEquals(1, orderCancelRecord.size());

        List<ConsumerRecord<String, String>> paymentCancelRecord = paymentCancelConsumer
                .awaitCompletion(Duration.ofSeconds(10))
                .getRecords();
        assertEquals(1, paymentCancelRecord.size());
    }
}
