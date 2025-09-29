package com.slimczes.orders.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.slimczes.orders.domain.exception.DomainException;
import com.slimczes.orders.domain.exception.OptimisticLockException;

public class Order {

    private final UUID id;
    private final UUID customerId;
    private OrderStatus status;
    private Integer version;
    private final List<OrderItem> items;
    private final Instant createdAt;
    private Instant updatedAt;

    public Order(UUID customerId) {
        this.id = UUID.randomUUID();
        this.customerId = validateCustomerId(customerId);
        this.status = OrderStatus.NEW;
        this.version = 0;
        this.items = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @Default
    public Order(UUID id, UUID customerId, OrderStatus status, Integer version, List<OrderItem> items,
                 Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.version = version;
        this.items = new ArrayList<>(items);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void createReservationItem(String sku, String name, int quantity) {
        validateModifiable();

        OrderItem item = new OrderItem(sku, name, quantity);
        items.add(item);
        this.updatedAt = Instant.now();
    }

    public void removeItem(UUID itemId) {
        validateModifiable();

        boolean removed = items.removeIf(item -> Objects.equals(item.getId(), itemId));
        if (!removed) {
            throw new DomainException("Order item not found: " + itemId);
        }
        this.updatedAt = Instant.now();
    }

    public void resolveOrderStatus(Integer expectedVersion) {
        validateVersionForUpdate(expectedVersion);
        validateCancelStatus();
        if (isAnyItemPending()) {
            this.status = OrderStatus.PENDING;
            this.version++;
            this.updatedAt = Instant.now();
            return;
        }
        this.status = OrderStatus.FULFILLED;
        this.version++;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (status == OrderStatus.FULFILLED) {
            throw new DomainException("Cannot cancel fulfilled order");
        }

        this.status = OrderStatus.CANCELLED;
        this.version++;
        this.updatedAt = Instant.now();
    }

    // Validation methods
    private void validateModifiable() {
        if (status != OrderStatus.NEW) {
            throw new DomainException("Cannot modify order in status: " + status);
        }
    }

    private void validateVersionForUpdate(Integer expectedVersion) {
        if (!Objects.equals(this.version, expectedVersion)) {
            throw new OptimisticLockException(
                "Version mismatch. Expected: " + expectedVersion + ", actual: " + version);
        }
    }

    private void validateCancelStatus() {
        switch (status) {
            case CANCELLED, FULFILLED -> {
                throw new DomainException("Cannot change status from " + status);
            }
        }
    }

    private boolean isAnyItemPending() {
        return items.stream().anyMatch(this::validateItem);
    }

    private boolean validateItem(OrderItem item) {
        return item.getStatus() == ItemStatus.PENDING;
    }

    private void validateItemParameters(String productSku, String name, int quantity) {
        if (productSku == null || productSku.trim().isEmpty()) {
            throw new IllegalArgumentException("Product SKU cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    private UUID validateCustomerId(UUID customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        return customerId;
    }

    private OrderItem findItemById(UUID itemId) {
        return items.stream()
                    .filter(item -> Objects.equals(item.getId(), itemId))
                    .findFirst()
                    .orElseThrow(() -> new DomainException("Order item not found: " + itemId));
    }

    private List<OrderItemSnapshot> getItemsSnapshot() {
        return items.stream()
                    .map(item -> new OrderItemSnapshot(item.getSku(), item.getItemName(), item.getQuantity()))
                    .toList();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Integer getVersion() {
        return version;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
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
