package com.example.order.infrastrutcture.messaging;

import com.example.order.application.dto.OrderResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class OrderKafkaProducer {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${spring.kafka.topic.order-send}")
    private String destinationTopic;

    public boolean sendMessage(OrderResponse order) {
        try {
            String payload = objectMapper.writeValueAsString(order);
            kafkaTemplate.send(destinationTopic, payload);
            return true;
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

        return false;
    }
}
