package com.slimczes.orders.api.customer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

import com.slimczes.orders.service.customer.CustomerService;
import com.slimczes.orders.service.customer.dto.CreateCustomerDto;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Path("/customer")
public class CustomerController {

    private final CustomerService customerService;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomer(CreateCustomerDto createCustomerDto) {
        UUID customerId = customerService.createCustomer(createCustomerDto);
        return Response.ok(new CreateCustomerResponse(customerId)).build();
    }


}
