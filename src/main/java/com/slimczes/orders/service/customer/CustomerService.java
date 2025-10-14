package com.slimczes.orders.service.customer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

import com.slimczes.orders.domain.model.Customer;
import com.slimczes.orders.domain.port.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CustomerService {

    private final CustomerRepository customerRepository;

    public UUID createCustomer(CreateCustomerDto customerDto) {
        return customerRepository.findByEmail(customerDto.email())
                .map(Customer::getId)
                .orElseGet(() -> {
                    Customer customer = new Customer(customerDto.email(), customerDto.name());
                    return customerRepository.save(customer).getId();
                });
    }
}
