package com.learning.products.command;

import com.learning.products.core.data.ProductLookupEntity;
import com.learning.products.core.data.ProductLookupRepository;
import com.learning.products.core.events.ProductCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;


@Component
@ProcessingGroup("product-group")
public class ProductLookupEventsHandler {

    private final ProductLookupRepository productLookupRepository;

    public ProductLookupEventsHandler(ProductLookupRepository repo) {
        this.productLookupRepository = repo;
    }

    @EventHandler
    public void on(ProductCreatedEvent event){
        ProductLookupEntity lookupEntity = new ProductLookupEntity(event.getProductId(), event.getTitle());

        productLookupRepository.save(lookupEntity);

    }
}
