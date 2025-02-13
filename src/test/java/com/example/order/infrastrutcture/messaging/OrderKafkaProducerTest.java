package com.example.order.infrastrutcture.messaging;

import com.example.order.application.dto.OrderItemResponse;
import com.example.order.application.dto.OrderResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderKafkaProducer orderKafkaProducer;

    @Value("${spring.kafka.topic.order-send}")
    private String destinationTopic = "order-completed";

    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        OrderItemResponse orderItem = OrderItemResponse.builder()
                .productSku("4321")
                .unitPrice(new BigDecimal(1.99))
                .quantity(1)
                .itemTotal(new BigDecimal(1.99))
                .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .vendorId("12345")
                .customerId("54321")
                .totalAmount(new BigDecimal(1.99))
                .orderItems(Arrays.asList(orderItem))
                .build();

        ReflectionTestUtils.setField(orderKafkaProducer, "destinationTopic", "order-completed");
    }

    @Test
    void testSendMessage_Success() throws JsonProcessingException {
        String payload = "{\"id\":1,\"status\":\"CALCULATED\"}";

        when(objectMapper.writeValueAsString(orderResponse)).thenReturn(payload);

        boolean result = orderKafkaProducer.sendMessage(orderResponse);

        verify(kafkaTemplate, times(1)).send(destinationTopic, payload);
    }

    @Test
    void testSendMessage_JsonProcessingException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(orderResponse)).thenThrow(JsonProcessingException.class);

        boolean result = orderKafkaProducer.sendMessage(orderResponse);

        verify(kafkaTemplate, times(0)).send(destinationTopic, "");
        assertFalse(result);
    }
}
