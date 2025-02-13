package com.example.order.domain.service;

import com.example.order.domain.model.Order;

import java.util.List;

public interface OrderService {

    Order saveOrder(Order order);

    Boolean idempotencyIdExists(String idempotencyId);

    List<Order> findCalculatedOrders();
}
