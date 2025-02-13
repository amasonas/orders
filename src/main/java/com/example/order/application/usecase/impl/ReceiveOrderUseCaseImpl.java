package com.example.order.application.usecase.impl;

import com.example.order.application.dto.OrderItemRequest;
import com.example.order.application.dto.OrderRequest;
import com.example.order.application.exceptions.IdempotencyViolationException;
import com.example.order.application.factory.OrderFactory;
import com.example.order.application.factory.OrderItemFactory;
import com.example.order.application.usecase.ReceiveOrderUseCase;
import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderItem;
import com.example.order.domain.model.OrderStatusEnum;
import com.example.order.domain.service.OrderItemService;
import com.example.order.domain.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiveOrderUseCaseImpl implements ReceiveOrderUseCase {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @Override
    public Order receiveOrder(OrderRequest request) {
        checkIdempotency(request.getIdempotencyId());

        final Order order = createOrderFromRequest(request);
        final List<OrderItem> items = createOrderItems(request.getOrderItems(), order);

        BigDecimal totalAmount = calculateTotalAmount(items);

        updateOrderWithTotalAmount(order, totalAmount);

        saveOrderWithItems(order, items);

        return order;
    }

    private void checkIdempotency(String idempotencyId) {
        if (orderService.idempotencyIdExists(idempotencyId)) {
            throw new IdempotencyViolationException(idempotencyId);
        }
    }

    private Order createOrderFromRequest(OrderRequest request) {
        return orderService.saveOrder(OrderFactory.build(request));
    }

    private List<OrderItem> createOrderItems(List<OrderItemRequest> orderItemsRequest, Order order) {
        List<OrderItem> items = OrderItemFactory.buildList(orderItemsRequest);
        items.forEach(item -> item.setOrder(order));
        return items;
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(item -> item
                        .getUnitPrice()
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void updateOrderWithTotalAmount(Order order, BigDecimal totalAmount) {
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatusEnum.CALCULATED);
    }

    private void saveOrderWithItems(Order order, List<OrderItem> items) {
        items.forEach(orderItem -> {
            BigDecimal totalItem = orderItem
                    .getUnitPrice()
                    .multiply(new BigDecimal(orderItem.getQuantity()));

            orderItem.setItemTotal(totalItem);

            orderItemService.saveOrderItem(orderItem);
        });
        orderService.saveOrder(order);
    }
}

