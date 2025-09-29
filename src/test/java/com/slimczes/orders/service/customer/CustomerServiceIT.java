package com.slimczes.orders.service.customer;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import com.slimczes.orders.domain.model.Customer;
import com.slimczes.orders.domain.port.repository.CustomerRepository;
import com.slimczes.orders.service.customer.dto.CreateCustomerDto;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class CustomerServiceIT {

    @Inject
    CustomerService customerService;

    @Inject
    CustomerRepository customerRepository;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "Test Customer";
    private static final UUID EXISTING_CUSTOMER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String EXISTING_EMAIL = "test.customer@example.com";

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up any test data that might interfere
        // The test data from Liquibase will be available due to test context
    }

    @Test
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
