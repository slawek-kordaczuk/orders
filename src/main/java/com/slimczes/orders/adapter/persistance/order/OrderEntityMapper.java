package com.slimczes.orders.adapter.persistance.order;

import com.slimczes.orders.adapter.persistance.order.entity.OrderEntity;
import com.slimczes.orders.adapter.persistance.order.entity.OrderItemEntity;
import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderEntityMapper {

    OrderEntity toOrderEntity(Order order);

    Order toOrderDomain(OrderEntity orderEntity);

    @Mapping(target = "order", ignore = true)
    OrderItemEntity toOrderItemEntity(OrderItem orderItem);

    OrderItem toOrderItemDomain(OrderItemEntity orderItemEntity);

    @AfterMapping
    default void linkOrderItems(@MappingTarget OrderEntity orderEntity) {
        if (orderEntity.getItems() != null) {
            orderEntity.getItems().forEach(item -> item.setOrder(orderEntity));
        }
    }
}
