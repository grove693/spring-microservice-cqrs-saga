package com.learning.orders.command.rest;

import lombok.Data;

@Data
public class CreateOrderPayload {

    private String productId;
    private Integer quantity;
    private String addressId;
}
