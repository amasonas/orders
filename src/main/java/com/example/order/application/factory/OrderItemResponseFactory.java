package com.example.order.application.factory;

import com.example.order.application.dto.OrderItemResponse;
import com.example.order.domain.model.OrderItem;

import java.util.List;

public class OrderItemResponseFactory {

    public static OrderItemResponse build(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .productSku(orderItem.getProductSku())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .itemTotal(orderItem.getItemTotal())
                .build();
    }

    public static List<OrderItemResponse> buildList(List<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(OrderItemResponseFactory::build)
                .toList();
    }
}
