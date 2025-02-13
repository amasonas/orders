package com.example.order.infrastrutcture.schedule;


import com.example.order.application.usecase.SendOrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class OrderBatchSenderScheduler {

    private final SendOrderUseCase sendOrderUseCase;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void sendOrders() {
        try {
            log.info("Starting the process to send batches of calculated orders");
            sendOrderUseCase.retrieveAndDispatchOrderBatches();
            log.info("Order batch processing completed successfully");
        } catch (Exception e) {
            log.error("Error occurred while sending order batches", e);
        }
    }
}
