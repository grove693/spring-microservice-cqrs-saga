package com.learning.orders.core.data;

import com.learning.orders.core.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<OrderEntity,String> {

    OrderEntity findByOrderId(String OrderId);
}
