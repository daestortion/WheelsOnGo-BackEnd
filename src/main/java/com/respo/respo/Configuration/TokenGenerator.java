package com.respo.respo.Configuration;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {

    private static final int TOKEN_LENGTH = 16; // Length of the token in bytes

    public static String generateResetToken(int userId) {
        // Generate a secure random token
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        new SecureRandom().nextBytes(tokenBytes);

        // Combine user ID and token bytes to create a unique token
        byte[] userIdBytes = String.valueOf(userId).getBytes();
        byte[] combinedBytes = new byte[userIdBytes.length + tokenBytes.length];
        System.arraycopy(userIdBytes, 0, combinedBytes, 0, userIdBytes.length);
        System.arraycopy(tokenBytes, 0, combinedBytes, userIdBytes.length, tokenBytes.length);

        // Encode the combined bytes using Base64
        return Base64.getEncoder().encodeToString(combinedBytes);
    }
}