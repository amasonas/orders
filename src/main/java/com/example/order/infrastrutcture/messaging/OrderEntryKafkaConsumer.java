package com.example.order.infrastrutcture.messaging;

import com.example.order.application.dto.OrderRequest;
import com.example.order.application.exceptions.IdempotencyViolationException;
import com.example.order.application.usecase.ReceiveOrderUseCase;
import com.example.order.utils.HashGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@EnableKafka
@RequiredArgsConstructor
public class OrderEntryKafkaConsumer {

    private final ReceiveOrderUseCase receiveOrderUseCase;
    private final ObjectMapper objectMapper;
    private final HashGenerator hashGenerator;

    @KafkaListener(topics = "order-entries", groupId = "order-consumer-group")
    public void listenOrderEntries(ConsumerRecord<String, String> record) {
        String message = record.value();
        String idempotencyId = generateIdempotencyKey(message);

        try {
            OrderRequest orderRequest = convertMessageToOrderRequest(message, idempotencyId);
            processOrderRequest(orderRequest);
        } catch (IdempotencyViolationException e) {
            handleIdempotencyViolation(e);
        } catch (Exception e) {
            handleProcessingError(e);
        }
    }

    private String generateIdempotencyKey(String message) {
        return hashGenerator.generateIdempotencyKey(message);
    }

    private OrderRequest convertMessageToOrderRequest(String message, String idempotencyId) throws Exception {
        OrderRequest orderRequest = objectMapper.readValue(message, OrderRequest.class);
        orderRequest.setIdempotencyId(idempotencyId);

        return orderRequest;
    }

    private void processOrderRequest(OrderRequest orderRequest) {
        log.info("Received order for customer: {}", orderRequest.getCustomerId());
        receiveOrderUseCase.receiveOrder(orderRequest);
    }

    private void handleIdempotencyViolation(IdempotencyViolationException e) {
        log.error("Duplicated message found with idempotency ID: {}", e.getMessage());
    }

    private void handleProcessingError(Exception e) {
        log.error("Failed to process the message");
        log.error("Error: {}", e.getMessage(), e);
    }
}

