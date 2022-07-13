package com.learning.orders.command;

import com.learning.orders.common.OrderStatus;
import com.learning.orders.core.events.OrderApprovedEvent;
import com.learning.orders.core.events.OrderCreatedEvent;
import com.learning.orders.core.events.OrderRejectedEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

@Aggregate
@Data
public class OrderAggregate {

    @AggregateIdentifier
    private  String orderId ;
    private  String userId;
    private  String productId;
    private  int quantity;
    private  String addressId;
    private  OrderStatus orderStatus;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderAggregate.class);

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {
        // can validate create order command

        LOGGER.info("Handling CreateOrderCommand....");
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);

        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        LOGGER.info("Handling OrderCreatedEvent....");
        BeanUtils.copyProperties(orderCreatedEvent, this);
    }

    @CommandHandler
    public void handle(ApproveOrderCommand approveOrderCommand) {
        LOGGER.info("Handling ApproveOrderCommand....");

        OrderApprovedEvent event = new OrderApprovedEvent(approveOrderCommand.getOrderId());

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderApprovedEvent event) {
        this.orderStatus = event.getOrderStatus();
    }

    @CommandHandler
    public void handle(RejectOrderCommand command) {
        LOGGER.info("Handling RejectOrderCommand....");

        OrderRejectedEvent event = new OrderRejectedEvent(command.getOrderId(), command.getReason());

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderRejectedEvent event) {
        this.orderStatus = event.getStatus();
    }
}
