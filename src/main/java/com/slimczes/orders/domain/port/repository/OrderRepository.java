package com.slimczes.orders.domain.port.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.model.OrderItem;

public interface OrderRepository {

    Order upsert(Order order);

    Optional<Order> findById(UUID orderId);

    Optional<OrderItem> findOrderItemBySku(String sku);

    List<Order> findByCustomerId(UUID customerId);

    void deleteById(UUID orderId);

}
