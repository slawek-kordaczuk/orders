package com.slimczes.orders.adapter.persistance.customer;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

import com.slimczes.orders.adapter.persistance.customer.entity.CustomerEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

@ApplicationScoped
public class CustomerRepositoryCrud implements PanacheRepositoryBase<CustomerEntity, UUID> {

    public CustomerEntity findByEmail(String email) {
        return find("email", email).firstResult();
    }
}
