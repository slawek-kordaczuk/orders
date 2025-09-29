package com.slimczes.orders.domain.port.repository;

import java.util.Optional;
import java.util.UUID;

import com.slimczes.orders.domain.model.Customer;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID customerId);
    Optional<Customer> findByEmail(String email);

}
