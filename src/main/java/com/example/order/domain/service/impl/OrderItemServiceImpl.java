package com.example.order.domain.service.impl;

import com.example.order.domain.model.OrderItem;
import com.example.order.domain.repository.OrderItemRepository;
import com.example.order.domain.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository repository;

    @Override
    public OrderItem saveOrderItem(OrderItem orderItem) {
        return repository.save(orderItem);
    }
}
