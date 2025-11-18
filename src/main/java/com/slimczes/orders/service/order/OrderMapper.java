package com.slimczes.orders.service.order;

import com.slimczes.orders.domain.event.OrderCancelEvent;
import com.slimczes.orders.domain.event.OrderCreateEvent;
import com.slimczes.orders.domain.event.PaymentCancelEvent;
import com.slimczes.orders.domain.event.PaymentCreateEvent;
import com.slimczes.orders.domain.model.Money;
import com.slimczes.orders.domain.model.Order;
import com.slimczes.orders.domain.model.OrderItem;
import com.slimczes.orders.domain.model.OrderItemSnapshot;
import com.slimczes.orders.service.order.dto.OrderReservedItem;
import com.slimczes.orders.service.order.dto.OrderResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
interface OrderMapper {

    @Mapping(target = "eventId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "orderId", source = "createdOrderId")
    @Mapping(target = "occurredAt", source = "order.createdAt")
    OrderCreateEvent toOrderCreated(Order order, UUID createdOrderId);

    @Mapping(target = "name", source = "itemName")
    OrderItemSnapshot toOrderItemSnapshot(OrderItem orderItem);

    @Mapping(target = "eventId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "occurredAt", source = "order.createdAt")
    OrderCancelEvent toOrderCancelled(Order order, String reason);

    OrderResponseDto toOrderResponseDto(Order order);

    @Mapping(target = "itemId", source = "id")
    @Mapping(target = "reservedQuantity", source = "quantity")
    @Mapping(target = "name", source = "itemName")
    OrderReservedItem toOrderReservedItem(OrderItem orderItem);

    @Mapping(target = "amount", source = "amount")
    PaymentCreateEvent toPaymentCreateEvent(UUID orderId, UUID clientId, Money amount);

    PaymentCancelEvent toPaymentCancelEvent(UUID orderId, UUID clientId);

}
