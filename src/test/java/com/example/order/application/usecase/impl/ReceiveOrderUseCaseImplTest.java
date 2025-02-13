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

        orderRequest = new OrderRequest();
        orderRequest.setIdempotencyId("00000000");
        orderRequest.setCustomerId("123456");
        orderRequest.setVendorId("123123");

        orderItemRequests = Arrays.asList(
                new OrderItemRequest("00005", 2, new BigDecimal("10.00")),
                new OrderItemRequest("00006", 3, new BigDecimal("20.00"))
        );

        orderRequest.setOrderItems(orderItemRequests);
        order = new Order();
        order.setId(1L);
        order.setCustomerId("123456");
        order.setVendorId("123123");

        orderItem = new OrderItem();
        orderItem.setProductSku("00005");
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(new BigDecimal("10.00"));

        order.setItems(Arrays.asList(orderItem));
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
