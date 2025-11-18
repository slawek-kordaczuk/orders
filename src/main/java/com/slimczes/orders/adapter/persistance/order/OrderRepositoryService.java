package com.slimczes.orders.adapter.persistance.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.slimczes.orders.adapter.persistance.order.entity.OrderEntity;
import com.slimczes.orders.adapter.persistance.order.entity.OrderItemEntity;
import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.model.OrderItem;
import com.slimczes.orders.domain.port.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Inject)
class OrderRepositoryService implements OrderRepository {

    private final OrderRepositoryCrud orderRepositoryCrud;
    private final OrderItemRepositoryCrud orderItemRepositoryCrud;
    private final OrderEntityMapper orderEntityMapper;

    @Override
    public Order upsert(Order order) {
        if (order.getId() == null) {
            OrderEntity entity = orderEntityMapper.toOrderEntity(order);
            orderRepositoryCrud.persist(entity);
            return orderEntityMapper.toOrderDomain(entity);
        }
        return orderEntityMapper
                .toOrderDomain(orderRepositoryCrud.findByIdOptional(order.getId())
                        .map(managedEntity -> {
                            managedEntity.setOrderStatus(order.getOrderStatus());
                            managedEntity.setPaymentStatus(order.getPaymentStatus());
                            managedEntity.setAreItemsReserved(order.getAreItemsReserved());
                            managedEntity.setIsPaymentCompleted(order.getIsPaymentCompleted());
                            managedEntity.setUpdatedAt(order.getUpdatedAt());
                            updateOrderItems(managedEntity, order);
                            return managedEntity;
                        }).orElseThrow(() -> {
                            log.error("Order with id {} not found for update", order.getId());
                            return new IllegalStateException("Order not found for update");
                        }));
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        Optional<OrderEntity> entity = orderRepositoryCrud.findByIdOptional(orderId);
        return entity.map(orderEntityMapper::toOrderDomain);
    }

    @Override
    public Optional<OrderItem> findOrderItemBySku(String sku) {
        OrderItemEntity entity = orderItemRepositoryCrud.findBySku(sku);
        return Optional.ofNullable(entity).map(orderEntityMapper::toOrderItemDomain);
    }

    @Override
    public List<Order> findByCustomerId(UUID customerId) {
        return orderRepositoryCrud.findByCustomerId(customerId).stream()
                .map(orderEntityMapper::toOrderDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID orderId) {
        orderRepositoryCrud.deleteById(orderId);
    }

    private void updateOrderItems(OrderEntity managedEntity, Order domainOrder) {
        domainOrder.getItems().forEach(item ->
                managedEntity.getItems().stream()
                        .filter(entityItem -> entityItem.getId().equals(item.getId()))
                        .findFirst()
                        .ifPresent(entityItem -> {
                            entityItem.setStatus(item.getStatus());
                            entityItem.setQuantity(item.getQuantity());
                        }));
    }
}
