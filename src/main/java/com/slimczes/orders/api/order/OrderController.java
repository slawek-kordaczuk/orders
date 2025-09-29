package com.slimczes.orders.api.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;

import com.slimczes.orders.service.order.CreateOrderService;
import com.slimczes.orders.service.order.dto.CancelOrderDto;
import com.slimczes.orders.service.order.dto.CreateOrderDto;
import com.slimczes.orders.service.order.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Path("/order")
public class OrderController {

    private final CreateOrderService createOrderService;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrder(CreateOrderDto createOrderDto) {
        UUID orderedId = createOrderService.createOrder(createOrderDto);
        return Response.ok(new CreateOrderResponse(orderedId)).build();
    }

    @POST
    @Path("/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cancelOrder(CancelOrderDto cancelOrderRequest) {
        createOrderService.cancelOrder(cancelOrderRequest);
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrder(@PathParam("id") UUID orderId) {
        OrderResponseDto orderResponseDto = createOrderService.getOrder(orderId);
        return Response.ok(orderResponseDto).build();
    }

}
