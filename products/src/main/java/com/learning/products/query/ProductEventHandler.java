package com.learning.products.query;

import com.learning.core.events.ProductReservationCancelledEvent;
import com.learning.core.events.ProductReservedEvent;
import com.learning.products.core.ProductEntity;
import com.learning.products.core.data.ProductsRepository;
import com.learning.products.core.events.ProductCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
public class ProductEventHandler {

    private final ProductsRepository productsRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventHandler.class);

    @Autowired
    public ProductEventHandler(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handleIllegalArgException(IllegalArgumentException ex) {
        // log error message
    }

    @ExceptionHandler(resultType = Exception.class)
    public void handleGeneralException(Exception ex) throws Exception {
        // log error message and throw it
        throw ex;
    }

    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent) throws Exception {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(productCreatedEvent, productEntity);

        productsRepository.save(productEntity);

       // throw new Exception("Exception thrown in product event handler");
    }

    @EventHandler
    public void on(ProductReservedEvent productReservedEvent) {
        ProductEntity productEntity = productsRepository.findByProductId(productReservedEvent.getProductId());
        productEntity.setQuantity(productReservedEvent.getQuantity());

        productsRepository.save(productEntity);

        LOGGER.info("Updated quantity for product with id {} for order id {}",
                productReservedEvent.getProductId(),
                productReservedEvent.getOrderId());
    }

    @EventHandler
    public void on(ProductReservationCancelledEvent event) {
        ProductEntity productEntity = productsRepository.findByProductId(event.getProductId());
        int newQuatity = productEntity.getQuantity() + event.getQuantity();

        productEntity.setQuantity(newQuatity);

        productsRepository.save(productEntity);

        LOGGER.info("ProductReservationCancelledEvent. Updated quantity for product with id {} for order id {} to {}",
                event.getProductId(),
                event.getOrderId(),
                newQuatity);

    }
}
