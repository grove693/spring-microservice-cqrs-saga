package com.learning.orders.query;

import com.learning.orders.core.OrderEntity;
import com.learning.orders.core.data.OrdersRepository;
import com.learning.orders.core.events.OrderApprovedEvent;
import com.learning.orders.core.events.OrderCreatedEvent;
import com.learning.orders.core.events.OrderRejectedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("order-group")
public class OrderEventHandler {

    private final OrdersRepository ordersRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventHandler.class);

    @Autowired
    public OrderEventHandler(OrdersRepository ordersRepository){
        this.ordersRepository = ordersRepository;
    }


    @EventHandler
    public void on(OrderCreatedEvent orderCreatedEvent) throws Exception {
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(orderCreatedEvent, orderEntity);

        ordersRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderApprovedEvent event) {
        OrderEntity entity = ordersRepository.findByOrderId(event.getOrderId());

        if (entity == null ){
            LOGGER.error("Order fetch from the db is null");
            // do smth about it
            return;
        }

        entity.setOrderStatus(event.getOrderStatus());
        ordersRepository.save(entity);
    }

    @EventHandler
    public void on(OrderRejectedEvent event) {
        OrderEntity entity = ordersRepository.findByOrderId(event.getOrderId());

        entity.setOrderStatus(event.getStatus());

        ordersRepository.save(entity);
    }
}