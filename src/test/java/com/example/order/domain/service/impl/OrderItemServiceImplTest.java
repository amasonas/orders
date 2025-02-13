package com.example.order.domain.service.impl;

import com.example.order.domain.model.OrderItem;
import com.example.order.domain.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository repository;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProductSku("12345");
        orderItem.setQuantity(10);
        orderItem.setUnitPrice(new BigDecimal("20.00"));
    }

    @Test
    void testSaveOrderItem() {
        when(repository.save(orderItem)).thenReturn(orderItem);

        OrderItem savedOrderItem = orderItemService.saveOrderItem(orderItem);

        assertNotNull(savedOrderItem);
    }
}
