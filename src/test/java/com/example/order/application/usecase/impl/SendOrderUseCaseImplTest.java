package com.example.order.application.usecase.impl;

import com.example.order.application.dto.OrderResponse;
import com.example.order.application.factory.OrderResponseFactory;
import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderItem;
import com.example.order.domain.service.OrderService;
import com.example.order.infrastrutcture.messaging.OrderKafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SendOrderUseCaseImplTest {
    @Mock
    private OrderService orderService;
    @Mock
    private OrderKafkaProducer orderKafkaProducer;
    @InjectMocks
    private SendOrderUseCaseImpl sendOrderUseCase;
    private Order orderSucess;
    private Order orderFail;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        OrderItem orderItem = OrderItem.builder()
                .productSku("4321")
                .unitPrice(new BigDecimal(1.99))
                .quantity(1)
                .itemTotal(new BigDecimal(1.99))
                .build();

        orderSucess = Order.builder()
                .vendorId("11111")
                .customerId("22222")
                .idempotencyId("0000")
                .totalAmount(new BigDecimal(1.99))
                .items(Arrays.asList(orderItem))
                .build();

        orderFail = Order.builder()
                .vendorId("3333")
                .customerId("4444")
                .idempotencyId("0000")
                .totalAmount(new BigDecimal(1.99))
                .items(Arrays.asList(orderItem))
                .build();

        when(orderKafkaProducer.sendMessage(OrderResponseFactory.build(orderSucess)))
                .thenReturn(true);

        when(orderKafkaProducer.sendMessage(OrderResponseFactory.build(orderFail)))
                .thenReturn(false);
    }

    @Test
    void testRetrieveAndDispatchOrderBatches() {
        List<Order> orderList = Arrays.asList(orderSucess, orderFail);
        when(orderService.findCalculatedOrders()).thenReturn(orderList);

        sendOrderUseCase.retrieveAndDispatchOrderBatches();

        verify(orderService, times(1)).findCalculatedOrders();
        verify(orderKafkaProducer, times(2)).sendMessage(any(OrderResponse.class));
    }
}
