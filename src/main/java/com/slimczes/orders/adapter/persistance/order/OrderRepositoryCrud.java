package com.slimczes.orders.adapter.persistance.order;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

import com.slimczes.orders.adapter.persistance.order.entity.OrderEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

@ApplicationScoped
public class OrderRepositoryCrud implements PanacheRepositoryBase<OrderEntity, UUID> {

    public List<OrderEntity> findByCustomerId(UUID customerId) {
        return list("customerId", customerId);
    }
}
