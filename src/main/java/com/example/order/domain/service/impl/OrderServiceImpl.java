package com.example.order.domain.service.impl;

import com.example.order.domain.model.Order;
import com.example.order.domain.repository.OrderRepository;
import com.example.order.domain.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final int BATCH_LIMIT_RECORDS = 300;
    private final OrderRepository repository;

    @Override
    public Order saveOrder(Order order) {

        return repository.save(order);
    }

    @Override
    public Boolean idempotencyIdExists(String idempotencyId) {
        return repository.findByIdempotencyId(idempotencyId).isPresent();
    }

    @Override
    public List<Order> findCalculatedOrders() {
        return repository.findCalculatedOrders(BATCH_LIMIT_RECORDS);
    }
}
