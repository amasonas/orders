package com.example.order.application.factory;

import com.example.order.application.dto.OrderResponse;
import com.example.order.domain.model.Order;

public class OrderResponseFactory {

    public static OrderResponse build(Order order) {
        return OrderResponse.builder()
                .vendorId(order.getVendorId())
                .customerId(order.getCustomerId())
                .totalAmount(order.getTotalAmount())
                .orderItems(OrderItemResponseFactory.buildList(order.getItems()))
                .build();
    }
}
