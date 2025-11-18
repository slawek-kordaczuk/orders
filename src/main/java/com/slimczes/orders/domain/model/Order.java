package com.slimczes.orders.domain.model;

import com.slimczes.orders.domain.exception.DomainException;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Order {

    private final UUID id;
    private final UUID customerId;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private Boolean isPaymentCompleted;
    private Boolean areItemsReserved;
    private final List<OrderItem> items;
    private final Instant createdAt;
    private Instant updatedAt;

    public Order(UUID customerId) {
        this.id = null;
        this.customerId = validateCustomerId(customerId);
        this.orderStatus = OrderStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
        this.areItemsReserved = false;
        this.isPaymentCompleted = false;
        this.items = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @Default
    public Order(UUID id, UUID customerId, OrderStatus orderStatus, PaymentStatus paymentStatus, Boolean isPaymentCompleted, Boolean areItemsReserved,
                 List<OrderItem> items, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.isPaymentCompleted = isPaymentCompleted;
        this.areItemsReserved = areItemsReserved;
        this.items = new ArrayList<>(items);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void createReservationItem(String sku, String name, int quantity) {
        OrderItem item = new OrderItem(sku, name, quantity);
        items.add(item);
        updatedAt = Instant.now();
    }

    public void markItemsReserved() {
        if (areItemsCompleted()) {
            areItemsReserved = true;
            updatedAt = Instant.now();
        }
    }

    public void markPaymentCompleted() {
        isPaymentCompleted = true;
        updatedAt = Instant.now();
    }

    public void updatePaymentStatus(PaymentStatus status) {
        paymentStatus = status;
        updatedAt = Instant.now();
    }

    public void resolveOrderStatus() {
        if (isPaymentCompleted() && areItemsReserved) {
            orderStatus = OrderStatus.FULFILLED;
            updatedAt = Instant.now();
        }
    }

    private boolean isPaymentCompleted() {
        return paymentStatus != PaymentStatus.PENDING;
    }

    public void cancel() {
        if (orderStatus == OrderStatus.FULFILLED) {
            throw new DomainException("Cannot cancel fulfilled order");
        }
        cancelItems();
        cancelPayment();
        orderStatus = OrderStatus.CANCELLED;
        updatedAt = Instant.now();
    }

    private void cancelItems() {
        items.forEach(item -> item.updateStatus(ItemStatus.CANCELED));
    }

    private void cancelPayment() {
        if (orderStatus == OrderStatus.FULFILLED) {
            throw new DomainException("Cannot cancel payment for fulfilled order");
        }
        paymentStatus = PaymentStatus.CANCELLED;
    }

    private boolean areItemsCompleted() {
        return items.stream().anyMatch(this::validateItem);
    }

    private boolean validateItem(OrderItem item) {
        return item.getStatus() != ItemStatus.PENDING;
    }

    private UUID validateCustomerId(UUID customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        return customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order order)) {
            return false;
        }
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
