package com.learning.orders.command;

import com.learning.orders.common.OrderStatus;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Builder
@Data
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    private final String orderId = UUID.randomUUID().toString();
    // hardcoded for now
    private final String userId = "27b95829-4f3f-4ddf-8983-151ba010e35b";
    private final String productId;
    private final int quantity;
    private final String addressId;
    private final OrderStatus orderStatus;
}
