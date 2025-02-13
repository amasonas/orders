package com.example.order.application.factory;

import com.example.order.application.dto.OrderItemRequest;
import com.example.order.domain.model.OrderItem;

import java.util.List;

public class OrderItemFactory {

    public static OrderItem build(OrderItemRequest dto) {
        return OrderItem.builder()
                .productSku(dto.getProductSku())
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .build();
    }

    public static List<OrderItem> buildList(List<OrderItemRequest> requestList) {
        return requestList.stream()
                .map(OrderItemFactory::build)
                .toList();
    }
}
