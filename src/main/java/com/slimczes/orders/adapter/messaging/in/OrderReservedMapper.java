package com.slimczes.orders.adapter.messaging.in;

import com.slimczes.orders.adapter.messaging.event.*;
import com.slimczes.orders.service.order.dto.*;
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

    PaidSucceedDto toPaidSucceedFromEvent(PaidEvent paidEvent);

    PaidFailedDto toPaidFailedFromEvent(PaidFailedEvent paidFailedEvent);

}
