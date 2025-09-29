package com.slimczes.orders.service.customer.mapper;

import com.slimczes.orders.domain.model.Customer;
import com.slimczes.orders.service.customer.dto.CreateCustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface CustomerMapper {

    Customer toCustomer(CreateCustomerDto customerDto);

}
