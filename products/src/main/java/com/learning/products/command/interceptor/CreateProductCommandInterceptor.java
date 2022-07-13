package com.learning.products.command.interceptor;

import com.learning.products.command.CreateProductCommand;
import com.learning.products.core.data.ProductLookupEntity;
import com.learning.products.core.data.ProductLookupRepository;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    private final ProductLookupRepository productLookupRepository;

    public CreateProductCommandInterceptor(ProductLookupRepository repo) {
        productLookupRepository = repo;
    }

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> list) {

        return (index, command) -> {

            LOGGER.info("Intercepted command {}", command.getPayloadType());
            if (CreateProductCommand.class.equals(command.getPayloadType())) {


                CreateProductCommand createCommand = (CreateProductCommand) command.getPayload();
                // check if the product already exists in the lookup db

                ProductLookupEntity entity = productLookupRepository.findByProductIdOrTitle(createCommand.getProductId(), createCommand.getTitle());

                if (entity != null) {
                    throw new IllegalStateException(
                            String.format("Product with product id % or title % already exists",
                                    entity.getProductId(),
                                    entity.getTitle()));
                }

            }

            return command;
        };
    }
}
