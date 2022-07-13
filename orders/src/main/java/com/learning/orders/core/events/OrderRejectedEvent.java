package com.learning.orders.core.events;

import com.learning.orders.common.OrderStatus;
import lombok.Value;

@Value
public class OrderRejectedEvent {

    private final String orderId;
    private final String reason;
    private final OrderStatus status = OrderStatus.REJECTED;
}
