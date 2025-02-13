package com.example.order.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OrderRequest {
    private String customerId;
    private String vendorId;
    private String idempotencyId;
    private List<OrderItemRequest> orderItems;
}
