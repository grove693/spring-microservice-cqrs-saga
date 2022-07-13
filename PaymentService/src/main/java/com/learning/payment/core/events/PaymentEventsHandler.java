package com.learning.payment.core.events;

import com.learning.core.events.PaymentProcessedEvent;
import com.learning.payment.core.PaymentEntity;
import com.learning.payment.core.data.PaymentRepository;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventsHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(PaymentEventsHandler.class);

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentEventsHandler(PaymentRepository repo){
        this.paymentRepository = repo;
    }

    @EventHandler
    public void on(PaymentProcessedEvent event) throws Exception {
        LOGGER.info("Handling PaymentProcessedEvent for order {}", event.getOrderId());
        PaymentEntity paymentEntity = new PaymentEntity();
        BeanUtils.copyProperties(event,paymentEntity);

        paymentRepository.save(paymentEntity);
    }

}
