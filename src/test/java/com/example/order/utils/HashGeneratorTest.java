package com.example.order.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    private final HashGenerator hashGenerator = new HashGenerator();

    @Test
    void testGenerateIdempotencyKey_ValidMessage() {
        String message = "{\"customerId\":\"12345\",\"vendorId\":\"67890\"}";
        String expectedHash = generateSHA256Hash(message);
        String actualHash = hashGenerator.generateIdempotencyKey(message);
        assertEquals(expectedHash, actualHash);
    }

    @Test
    void testGenerateIdempotencyKey_NullMessage() {
        String message = null;
        assertThrows(RuntimeException.class, () -> hashGenerator.generateIdempotencyKey(message));
    }

    private String generateSHA256Hash(String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(message == null ? new byte[0] : message.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating SHA-256 hash for test.", e);
        }
    }
}