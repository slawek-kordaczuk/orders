package com.slimczes.orders.adapter.persistance.customer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import com.slimczes.orders.domain.model.Customer;
import com.slimczes.orders.domain.port.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Inject)
class CustomerRepositoryService implements CustomerRepository {

    private final CustomerRepositoryCrud customerRepositoryCrud;
    private final CustomerEntityMapper customerEntityMapper;

    @Override
    @Transactional
    public Customer save(Customer customer) {
        CustomerEntity entity = customerEntityMapper.toCustomerEntity(customer);
        customerRepositoryCrud.persist(entity);
        return customerEntityMapper.toCustomerDomain(entity);
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        return customerRepositoryCrud.findByIdOptional(customerId)
                                     .map(customerEntityMapper::toCustomerDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        CustomerEntity entity = customerRepositoryCrud.findByEmail(email);
        return Optional.ofNullable(entity)
                       .map(customerEntityMapper::toCustomerDomain);
    }
}
