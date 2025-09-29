package com.slimczes.orders.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Customer {
    private final UUID id;
    private final String email;
    private String name;
    private boolean deleted;
    private final Instant createdAt;
    private Instant updatedAt;

    // Constructor for new customers
    public Customer(String email, String name) {
        this.id = UUID.randomUUID();
        this.email = validateEmail(email);
        this.name = name;
        this.deleted = false;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Constructor for existing customers (from persistence)
//    public Customer(UUID id, String email, String name, boolean deleted, Instant createdAt, Instant updatedAt) {
//        this.id = id;
//        this.email = email;
//        this.name = name;
//        this.deleted = deleted;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }

    // Business methods
    public void updateProfile(String name) {
        this.name = name;
        this.updatedAt = Instant.now();
    }

    public void markAsDeleted() {
        if (deleted) {
            throw new IllegalStateException("Customer is already deleted");
        }
        this.deleted = true;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return !deleted;
    }

    private String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return email.toLowerCase().trim();
    }

    // Getters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public boolean isDeleted() { return deleted; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
