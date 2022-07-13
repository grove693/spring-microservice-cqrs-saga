package com.learning.query;

import com.learning.core.model.PaymentDetails;
import com.learning.core.model.User;
import com.learning.core.query.FetchUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserEventsHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(UserEventsHandler.class);

    @QueryHandler
    public User handleQuery(FetchUserPaymentDetailsQuery query) {
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("SERGEY KARGOPOLOV")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();

        User userRest = User.builder()
                .firstName("Sergey")
                .lastName("Kargopolov")
                .userId(query.getUserId())
                .paymentDetails(paymentDetails)
                .build();

        LOGGER.info("Succesfully returning user payment details");

        return userRest;
    }

}
