package com.learning.orders.command.rest;

import com.learning.orders.command.CreateOrderCommand;
import com.learning.orders.common.OrderStatus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {

    private final CommandGateway commandGateway;

    @Autowired
    public OrdersCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createOrder(@Valid @RequestBody CreateOrderPayload createOrderPayload) {
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .quantity(createOrderPayload.getQuantity())
                .addressId(createOrderPayload.getAddressId())
                .productId(createOrderPayload.getProductId())
                .orderStatus(OrderStatus.CREATED)
                .build();

        return commandGateway.sendAndWait(createOrderCommand);
    }

}
