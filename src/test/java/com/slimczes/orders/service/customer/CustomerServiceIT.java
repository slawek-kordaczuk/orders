package com.slimczes.orders.service.customer;

import com.slimczes.orders.domain.model.Customer;
import com.slimczes.orders.domain.port.repository.CustomerRepository;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class CustomerServiceIT {

    @Inject
    CustomerService customerService;

    @Inject
    CustomerRepository customerRepository;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "Test Customer";

    @Test
    @TestTransaction
    void shouldCreateCustomerSuccessfully() {
        // Given
        CreateCustomerDto createCustomerDto = new CreateCustomerDto(TEST_EMAIL, TEST_NAME);

        // When
        UUID customerId = customerService.createCustomer(createCustomerDto);

        // Then
        assertNotNull(customerId);

        Optional<Customer> savedCustomer = customerRepository.findById(customerId);
        assertTrue(savedCustomer.isPresent());
        assertEquals(TEST_EMAIL, savedCustomer.get().getEmail());
        assertEquals(TEST_NAME, savedCustomer.get().getName());
        assertFalse(savedCustomer.get().isDeleted());
        assertNotNull(savedCustomer.get().getCreatedAt());
        assertNotNull(savedCustomer.get().getUpdatedAt());
    }
}
