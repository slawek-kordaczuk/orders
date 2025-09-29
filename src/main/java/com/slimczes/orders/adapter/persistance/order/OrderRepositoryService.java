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
public class OrderRepositoryService implements OrderRepository {

    private final OrderRepositoryCrud orderRepositoryCrud;
    private final OrderItemRepositoryCrud orderItemRepositoryCrud;
    private final OrderEntityMapper orderEntityMapper;

    @Override
    @Transactional
    public Order upsert(Order order) {
        return orderEntityMapper
            .toOrderDomain(orderRepositoryCrud.findByIdOptional(order.getId())
                                              .map(managedEntity -> {
                                                  managedEntity.setStatus(order.getStatus());
                                                  managedEntity.setUpdatedAt(order.getUpdatedAt());
                                                  managedEntity.setVersion(order.getVersion());
                                                  updateOrderItems(managedEntity, order);
                                                  return managedEntity;
                                              }).orElseGet(() -> {
                    OrderEntity entity = orderEntityMapper.toOrderEntity(order);
                    orderRepositoryCrud.persist(entity);
                    return entity;
                }));
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

    @Override
    @Transactional
    public Optional<Order> findById(UUID orderId) {
        Optional<OrderEntity> entity = orderRepositoryCrud.findByIdOptional(orderId);
        return entity.map(orderEntityMapper::toOrderDomain);
    }

    @Override
    @Transactional
    public Optional<OrderItem> findOrderItemBySku(String sku) {
        OrderItemEntity entity = orderItemRepositoryCrud.findBySku(sku);
        return Optional.ofNullable(entity).map(orderEntityMapper::toOrderItemDomain);
    }

    @Override
    @Transactional
    public List<Order> findByCustomerId(UUID customerId) {
        return orderRepositoryCrud.findByCustomerId(customerId).stream()
                                  .map(orderEntityMapper::toOrderDomain)
                                  .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID orderId) {
        orderRepositoryCrud.deleteById(orderId);
    }
}
