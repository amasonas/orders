package com.example.order.utils;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;

@Log4j2
@Service
public class HashGenerator {

    public String generateIdempotencyKey(String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(message.getBytes());

            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            log.error("Failed to generate IdempotencyKey due to missing algorithm");
            throw new RuntimeException("Error generating hash for idempotency key.", e);
        }
    }
}
