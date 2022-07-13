package com.learning.payment.command;

import com.learning.core.commands.ProcessPaymentCommand;
import com.learning.core.model.PaymentDetails;
import com.learning.core.events.PaymentProcessedEvent;
import com.learning.payment.core.events.PaymentEventsHandler;
import lombok.Data;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

@Aggregate
@Data
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;
    private String orderId;
    private PaymentDetails paymentDetails;

    private final Logger LOGGER = LoggerFactory.getLogger(PaymentAggregate.class);


    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
        // can validate ProcessPaymentCommand
        LOGGER.info("Handling ProcessPaymentCommand");
        if (!isValid(processPaymentCommand)) {
            LOGGER.error("Invalid ProcessPaymentCommand");
            throw new IllegalArgumentException("Process payment command is invalid");
        }
        PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent();
        BeanUtils.copyProperties(processPaymentCommand, paymentProcessedEvent);

        AggregateLifecycle.apply(paymentProcessedEvent);
    }

    private boolean isValid(ProcessPaymentCommand processPaymentCommand) {
        return StringUtils.hasText(processPaymentCommand.getPaymentId())
                && StringUtils.hasText(processPaymentCommand.getOrderId())
                && processPaymentCommand.getPaymentDetails() != null;
    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent event) {
        LOGGER.info("Handling PaymentProcessedEvent for order {}", event.getOrderId());
        BeanUtils.copyProperties(event, this);
    }

}
