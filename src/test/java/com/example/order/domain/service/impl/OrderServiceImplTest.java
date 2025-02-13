package com.example.order.domain.service.impl;

import com.example.order.domain.model.Order;
import com.example.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class OrderServiceImplTest {

    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        order = new Order();
        order.setId(1L);
        order.setCustomerId("12345");
        order.setVendorId("67890");
    }

    @Test
    void testSaveOrder() {
        when(repository.save(order)).thenReturn(order);

        Order savedOrder = orderService.saveOrder(order);

        assertNotNull(savedOrder);
    }

    @Test
    void testFindCalculatedOrders() {
        when(repository.findCalculatedOrders(1))
                .thenReturn(Arrays.asList(order));

        List<Order> calculatedOrders = orderService.findCalculatedOrders();

        assertNotNull(calculatedOrders);
    }

    @Test
    void testIdempotencyIdExists() {
        when(repository.findByIdempotencyId("9999"))
                .thenReturn(java.util.Optional.of(order));

        Boolean exists = orderService.idempotencyIdExists("9999");

        assertTrue(exists);
    }

}
