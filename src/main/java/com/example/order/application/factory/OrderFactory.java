package com.example.order.application.factory;

import com.example.order.application.dto.OrderRequest;
import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderStatusEnum;

import java.time.LocalDateTime;

public class OrderFactory {

    public static Order build(OrderRequest request) {
        return Order.builder()
                .customerId(request.getCustomerId())
                .vendorId(request.getVendorId())
                .status(OrderStatusEnum.RECEIVED)
                .idempotencyId(request.getIdempotencyId())
                .creationDate(LocalDateTime.now())
                .build();
    }
}
