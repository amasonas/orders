package com.example.order.infrastrutcture.messaging;

import com.example.order.application.dto.OrderRequest;
import com.example.order.application.exceptions.IdempotencyViolationException;
import com.example.order.application.usecase.ReceiveOrderUseCase;
import com.example.order.utils.HashGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderEntryKafkaConsumerTest {

    private static final String VALID_MESSAGE = "{\"customerId\":\"12345\",\"vendorId\":\"67890\"}";
    private static final String IDEMPOTENCY_ID = "0000000";
    @Mock
    private ReceiveOrderUseCase receiveOrderUseCase;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private HashGenerator hashGenerator;
    @InjectMocks
    private OrderEntryKafkaConsumer orderEntryKafkaConsumer;
    @Mock
    private ConsumerRecord<String, String> record;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListenOrderEntries_Success() throws Exception {
        when(record.value()).thenReturn(VALID_MESSAGE);
        when(hashGenerator.generateIdempotencyKey(VALID_MESSAGE)).thenReturn(IDEMPOTENCY_ID);
        OrderRequest orderRequestMOck = new OrderRequest();
        orderRequestMOck.setCustomerId("12345");
        orderRequestMOck.setVendorId("67890");
        orderRequestMOck.setIdempotencyId(IDEMPOTENCY_ID);

        when(objectMapper.readValue(VALID_MESSAGE, OrderRequest.class)).thenReturn(orderRequestMOck);

        orderEntryKafkaConsumer.listenOrderEntries(record);

        verify(receiveOrderUseCase, times(1)).receiveOrder(orderRequestMOck);
    }

    @Test
    void testListenOrderEntries_IdempotencyViolation() throws Exception {
        when(record.value()).thenReturn(VALID_MESSAGE);
        when(hashGenerator.generateIdempotencyKey(VALID_MESSAGE))
                .thenReturn(IDEMPOTENCY_ID);
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId("12345");
        orderRequest.setVendorId("67890");
        orderRequest.setIdempotencyId(IDEMPOTENCY_ID);
        when(objectMapper.readValue(VALID_MESSAGE, OrderRequest.class))
                .thenReturn(orderRequest);

        doThrow(new IdempotencyViolationException(IDEMPOTENCY_ID))
                .when(receiveOrderUseCase)
                .receiveOrder(orderRequest);

        orderEntryKafkaConsumer.listenOrderEntries(record);

        verify(receiveOrderUseCase, times(1)).receiveOrder(orderRequest);
    }

    @Test
    void testHandleProcessingError() throws Exception {
        when(record.value()).thenReturn(VALID_MESSAGE);
        when(hashGenerator.generateIdempotencyKey(VALID_MESSAGE)).thenReturn(IDEMPOTENCY_ID);

        orderEntryKafkaConsumer.listenOrderEntries(record);

        verify(receiveOrderUseCase, never()).receiveOrder(any());
    }
}
