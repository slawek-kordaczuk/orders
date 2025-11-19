package com.slimczes.orders.adapter.messaging.in;

import com.slimczes.orders.adapter.messaging.event.*;
import com.slimczes.orders.service.order.dto.*;
import com.slimczes.orders.service.payment.dto.PaidFailedDto;
import com.slimczes.orders.service.payment.dto.PaidSucceedDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
interface OrderReservedMapper {

    OrderReservedDto toOrderReservedFromEvent(ItemsReserved itemsReserved);

    OrderReservedItem toOrderReservedItem(ReservedItem reservedItem);

    OrderReservedFailedDto toOrderReservedCancelFromEvent(ItemReservationFailed itemReservationFailed);

    OrderReservedFailedItem toOrderReservedCancelItem(ItemFailed itemFailed);

    PaidSucceedDto toPaidSucceedFromEvent(PaidEvent paidEvent);

    PaidFailedDto toPaidFailedFromEvent(PaidFailedEvent paidFailedEvent);

}
