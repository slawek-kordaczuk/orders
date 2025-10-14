package com.slimczes.orders.adapter.persistance.customer;

import com.slimczes.orders.domain.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
interface CustomerEntityMapper {

    CustomerEntity toCustomerEntity(Customer customer);

    Customer toCustomerDomain(CustomerEntity customerEntity);
}
