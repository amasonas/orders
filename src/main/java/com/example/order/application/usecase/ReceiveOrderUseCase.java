package com.example.order.application.usecase;

import com.example.order.application.dto.OrderRequest;
import com.example.order.domain.model.Order;

public interface ReceiveOrderUseCase {

    Order receiveOrder(OrderRequest request);
}
