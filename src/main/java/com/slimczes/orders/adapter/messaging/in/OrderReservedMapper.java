package com.slimczes.orders.adapter.messaging.in;

import com.slimczes.orders.adapter.messaging.event.ItemFailed;
import com.slimczes.orders.adapter.messaging.event.ItemReservationFailed;
import com.slimczes.orders.adapter.messaging.event.ItemsReserved;
import com.slimczes.orders.adapter.messaging.event.ReservedItem;
import com.slimczes.orders.service.order.dto.OrderReservedFailedDto;
import com.slimczes.orders.service.order.dto.OrderReservedFailedItem;
import com.slimczes.orders.service.order.dto.OrderReservedDto;
import com.slimczes.orders.service.order.dto.OrderReservedItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
interface OrderReservedMapper {

    OrderReservedDto toOrderReservedFromEvent(ItemsReserved itemsReserved);

    OrderReservedItem toOrderReservedItem(ReservedItem reservedItem);

    OrderReservedFailedDto toOrderReservedCancelFromEvent(ItemReservationFailed itemReservationFailed);

    OrderReservedFailedItem toOrderReservedCancelItem(ItemFailed itemFailed);

}
