package com.learning.products.query.rest;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ProductRestModel implements Serializable {

    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;
}
