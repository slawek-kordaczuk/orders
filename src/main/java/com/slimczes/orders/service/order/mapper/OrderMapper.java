package com.slimczes.orders.service.order.mapper;

import com.slimczes.orders.domain.event.OrderCancelled;
import com.slimczes.orders.domain.event.OrderCreated;
import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.model.OrderItem;
import com.slimczes.orders.domain.model.OrderItemSnapshot;
import com.slimczes.orders.service.order.dto.OrderReservedItem;
import com.slimczes.orders.service.order.dto.OrderResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface OrderMapper {

    @Mapping(target = "eventId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "occurredAt", source = "createdAt")
    OrderCreated toOrderCreated(Order order);

    @Mapping(target = "name", source = "itemName")
    OrderItemSnapshot toOrderItemSnapshot(OrderItem orderItem);

    @Mapping(target = "eventId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "occurredAt", source = "order.createdAt")
    OrderCancelled toOrderCancelled(Order order, String reason);

    OrderResponseDto toOrderResponseDto(Order order);

    @Mapping(target = "itemId", source = "id")
    @Mapping(target = "reservedQuantity", source = "quantity")
    @Mapping(target = "name", source = "itemName")
    OrderReservedItem toOrderReservedItem(OrderItem orderItem);

}
