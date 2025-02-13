package com.example.order.application.usecase.impl;

import com.example.order.application.dto.OrderItemRequest;
import com.example.order.application.dto.OrderRequest;
import com.example.order.application.exceptions.IdempotencyViolationException;
import com.example.order.application.factory.OrderFactory;
import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderItem;
import com.example.order.domain.service.impl.OrderItemServiceImpl;
import com.example.order.domain.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ReceiveOrderUseCaseImplTest {

    @Mock
    private OrderServiceImpl orderService;

    @Mock
    private OrderItemServiceImpl orderItemService;

    @Mock
    private OrderFactory orderFactory;

    @InjectMocks
    private ReceiveOrderUseCaseImpl receiveOrderUseCase;

    private OrderRequest orderRequest;
    private Order order;
    private OrderItem orderItem;
    private List<OrderItemRequest> orderItemRequests;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        orderItemRequests = Arrays.asList(
                OrderItemRequest.builder()
                        .productSku("00005")
                        .quantity(2)
                        .unitPrice(new BigDecimal("10.00"))
                        .build(),
                OrderItemRequest.builder()
                        .productSku("00006")
                        .quantity(3)
                        .unitPrice(new BigDecimal("20.00"))
                        .build()
        );

        orderRequest = OrderRequest.builder()
                .idempotencyId("00000000")
                .customerId("123456")
                .vendorId("123123")
                .orderItems(orderItemRequests)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .productSku("00005")
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        order = Order.builder()
                .id(1L)
                .customerId("123456")
                .vendorId("123123")
                .items(Arrays.asList(orderItem))
                .build();

    }

    @Test
    public void testReceiveOrder_Success() {
        when(orderService.idempotencyIdExists("00000000")).thenReturn(false);
        when(orderService.saveOrder(any(Order.class))).thenReturn(order);
        when(orderItemService.saveOrderItem(any(OrderItem.class))).thenReturn(orderItem);

        Order savedOrder = receiveOrderUseCase.receiveOrder(orderRequest);

        assertNotNull(savedOrder);
    }

    @Test
    public void testReceiveOrder_IdempotencyViolation() {
        orderRequest.setIdempotencyId("111111111");
        when(orderService.idempotencyIdExists("111111111")).thenReturn(true);

        assertThrows(IdempotencyViolationException.class, () -> {
            receiveOrderUseCase.receiveOrder(orderRequest);
        });
    }


}
