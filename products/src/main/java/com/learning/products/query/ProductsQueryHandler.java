package com.learning.products.query;

import com.learning.products.core.data.ProductsRepository;
import com.learning.products.query.rest.ProductRestModel;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ProcessingGroup("product-group")
public class ProductsQueryHandler {

    private final ProductsRepository prodRepo;

    @Autowired
    public ProductsQueryHandler(ProductsRepository repo) {
        this.prodRepo = repo;
    }

    @QueryHandler
    public List<ProductRestModel> findProducts(FindProductsQuery query) {
        return prodRepo
                .findAll()
                .stream()
                .map(productEntity -> {
                    ProductRestModel prod = new ProductRestModel();
                    BeanUtils.copyProperties(productEntity, prod);

                    return prod;
                })
                .collect(Collectors.toList());
    }
}
