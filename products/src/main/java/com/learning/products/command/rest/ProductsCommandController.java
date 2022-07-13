package com.learning.products.command.rest;

import com.learning.products.command.CreateProductCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductsCommandController {

    @Autowired
    ProductsCommandController(Environment env, CommandGateway cmdGateway) {
        this.env = env;
        this.commandGateway = cmdGateway;
    }
    private Environment env;
    private CommandGateway commandGateway;
    @PostMapping
    public String createProduct(@Valid  @RequestBody  CreateProductModel createProductModel){
        CreateProductCommand createProdCmd = CreateProductCommand.builder()
                .price(createProductModel.getPrice())
                .title(createProductModel.getTitle())
                .quantity(createProductModel.getQuantity())
                .productId(UUID.randomUUID().toString())
                .build();

        String ret = commandGateway.sendAndWait(createProdCmd);
      /*  try {
            ret = commandGateway.sendAndWait(createProdCmd);
        } catch (Exception ex) {
            ret = ex.getLocalizedMessage();
        }*/
        return ret;
    }

    @PutMapping
    public String updateProduct() {
        return "HTTP PUT";
    }

    @DeleteMapping
    public String deleteProduct() {
        return "HTTP Delete";
    }
}
