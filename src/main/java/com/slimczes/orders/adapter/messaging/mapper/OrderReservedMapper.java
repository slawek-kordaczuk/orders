package com.slimczes.orders.adapter.messaging.mapper;

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
public interface OrderReservedMapper {

    OrderReservedDto toOrderReservedFromEvent(ItemsReserved itemsReserved);

    OrderReservedItem toOrderReservedItem(ReservedItem reservedItem);

    @Mapping(source = "itemFailed", target = "failedItems")
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = "FAILED")
    OrderReservedFailedDto toOrderReservedCancelFromEvent(ItemReservationFailed itemReservationFailed);

    OrderReservedFailedItem toOrderReservedCancelItem(ItemFailed itemFailed);

}
