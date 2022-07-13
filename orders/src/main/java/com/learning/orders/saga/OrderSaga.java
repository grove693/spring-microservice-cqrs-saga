package com.learning.orders.saga;

import com.learning.core.commands.CancelProductReservationCommand;
import com.learning.core.commands.ProcessPaymentCommand;
import com.learning.core.commands.ReserveProductCommand;
import com.learning.core.events.PaymentProcessedEvent;
import com.learning.core.events.ProductReservationCancelledEvent;
import com.learning.core.events.ProductReservedEvent;
import com.learning.core.model.User;
import com.learning.core.query.FetchUserPaymentDetailsQuery;
import com.learning.orders.command.ApproveOrderCommand;
import com.learning.orders.command.RejectOrderCommand;
import com.learning.orders.core.events.OrderApprovedEvent;
import com.learning.orders.core.events.OrderCreatedEvent;
import com.learning.orders.core.events.OrderRejectedEvent;
import org.axonframework.commandhandling.callbacks.LoggingCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();
        LOGGER.info("OrderCreatedEvent for orderId: {} productId: {}",reserveProductCommand.getOrderId(), reserveProductCommand.getProductId());


        commandGateway.send(reserveProductCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                // start compensating tx
                LOGGER.info("Reserve product failed. Starting compensating tx. OrderId: {} productId: {}",reserveProductCommand.getOrderId(), reserveProductCommand.getProductId());

                RejectOrderCommand command = new RejectOrderCommand(orderCreatedEvent.getOrderId(), commandResultMessage.exceptionResult().getMessage());
                commandGateway.send(command);

            }
            LOGGER.info("Command handler method for reserve prod command");
        });

        //commandGateway.send(reserveProductCommand, LoggingCallback.INSTANCE);

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        // process user payment
        LOGGER.info("ProductReservedEvent handler called for prod id {} and order id {}",
                productReservedEvent.getProductId(),
                productReservedEvent.getOrderId());

        FetchUserPaymentDetailsQuery query = new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());

        User userPaymentDetails = null;

        try {

            userPaymentDetails = queryGateway.query(query, ResponseTypes.instanceOf(User.class)).join();

            if (userPaymentDetails == null) {
                LOGGER.info("User payment details null. Starting compensating tx");
                // start compensating tx
                cancelProductReservation(productReservedEvent,"Payment details is null");
                return;
            }

            LOGGER.info("Sucessfully fetched payment details for user {}", userPaymentDetails.getFirstName());

        } catch (Exception ex) {
            LOGGER.error("Error during payment details fetch", ex);

            // start compensating transaction
            cancelProductReservation(productReservedEvent,"Payment details fetch error");
            return;
        }

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();

        String processPaymentCmdResult;
        try {
            LOGGER.info("Sending ProcessPayment Command");

            processPaymentCmdResult = commandGateway.sendAndWait(processPaymentCommand,10, TimeUnit.SECONDS);

            if (processPaymentCmdResult == null || processPaymentCmdResult.isEmpty()) {
                LOGGER.info("Process paymend Command result is null. Start compensating tx");
                // start compensating tx
                cancelProductReservation(productReservedEvent,"Process Payment Command result is null");
                return;
            }

        } catch (Exception ex) {
            LOGGER.error("Error during process payment command processing. Starting compensating tx",ex);
            // start compensating tx
            cancelProductReservation(productReservedEvent,"Error during processing payment");
            return;
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent event) {
        LOGGER.info("Sending ApproveOrder command...");
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(event.getOrderId());

        commandGateway.send(approveOrderCommand, LoggingCallback.INSTANCE);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent event) {
        LOGGER.info("Order is approved.Order sage complete. Order id {}", event.getOrderId());

        //SagaLifecycle.end()

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent event) {
        LOGGER.info("Sending RejectOrder command for order id {}...", event.getOrderId());
        RejectOrderCommand command = new RejectOrderCommand(event.getOrderId(), event.getReason());

        commandGateway.send(command);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent event) {
        LOGGER.info("Sucessfully rejected order with id {}...", event.getOrderId());

    }

    private void cancelProductReservation(ProductReservedEvent event, String reason) {
        CancelProductReservationCommand command = CancelProductReservationCommand.builder()
                .orderId(event.getOrderId())
                .productId(event.getProductId())
                .userId(event.getUserId())
                .reason(reason)
                .build();

        commandGateway.send(command);
    }
}
