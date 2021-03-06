# Demo Spring Microservice App

### Description

A test project showcasing CQRS and SAGA design patterns. Developed using Spring cloud and Axxon framework

The test project comprises of the following microservices:
 - Discovery Service
 - API Gateway Service
 - Order Service
 - Product Service
 - User Service
 - Payment Service
 
 Tech Stack:
  - Java 14
  - Spring boot 
  - Spring cloud
  - Axxon Framework
  
### Useful info

The REST Endpoints from the microservices are acceseed through the API Gateway.
The API gateway port is 8882

*Example* of endpoints:
localhost:8082/products-service/products
localhost:8082/orders-service/orders

The microservice name can be found under *application.properties* under the property *spring.application.name*

H2 is used as a data store, its location must be configured in *application.properties* under *spring.datasource.url*
TODO: Configure the h2 path to be relative instead absolute

### Steps to run
1. Download the axxon framework jar and run it
2. Run the microservices: Discover, API Gateway, order , product, payment, user
