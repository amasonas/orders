package com.example.order.application.usecase.impl;

import com.example.order.application.dto.OrderResponse;
import com.example.order.application.factory.OrderResponseFactory;
import com.example.order.application.usecase.SendOrderUseCase;
import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderStatusEnum;
import com.example.order.domain.service.OrderService;
import com.example.order.infrastrutcture.messaging.OrderKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class SendOrderUseCaseImpl implements SendOrderUseCase {

    private final OrderService orderService;
    private final OrderKafkaProducer orderKafkaProducer;

    @Override
    public void retrieveAndDispatchOrderBatches() {
        List<Order> orderList = orderService.findCalculatedOrders();

        orderList.forEach(this::processRecord);
    }

    private void processRecord(Order order) {
        log.info("Sending order with idempotencyId: {}", order.getIdempotencyId());

        OrderResponse response = OrderResponseFactory.build(order);
        boolean success = orderKafkaProducer.sendMessage(response);

        updateOrderStatus(order, success);
        orderService.saveOrder(order);
    }

    private void updateOrderStatus(Order order, boolean success) {
        OrderStatusEnum status = success ? OrderStatusEnum.INTEGRATED : OrderStatusEnum.ERROR;
        order.setStatus(status);
        log.info("Order status updated to: {}", status);
    }
}

