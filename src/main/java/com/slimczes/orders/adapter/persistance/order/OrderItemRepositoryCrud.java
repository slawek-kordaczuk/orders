package com.slimczes.orders.adapter.persistance.order;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

import com.slimczes.orders.adapter.persistance.order.entity.OrderItemEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

@ApplicationScoped
public class OrderItemRepositoryCrud implements PanacheRepositoryBase<OrderItemEntity, UUID> {

    public OrderItemEntity findBySku(String sku) {
        return find("sku", sku).firstResult();
    }
}
