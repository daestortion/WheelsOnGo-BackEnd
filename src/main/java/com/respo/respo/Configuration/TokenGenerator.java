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

    /**
     * Validates the token by extracting the user ID and ensuring it matches the expected userId.
     * 
     * @param token  The token to validate.
     * @param userId The user ID to validate against.
     * @return True if the token is valid and matches the user ID, false otherwise.
     */
    public static boolean validateToken(String token, int userId) {
        byte[] decodedBytes = Base64.getDecoder().decode(token);
        byte[] userIdBytes = String.valueOf(userId).getBytes();

        // Ensure the token has the correct length (user ID bytes + random token bytes)
        if (decodedBytes.length != userIdBytes.length + TOKEN_LENGTH) {
            return false;
        }

        // Check if the token's user ID portion matches the provided user ID
        for (int i = 0; i < userIdBytes.length; i++) {
            if (decodedBytes[i] != userIdBytes[i]) {
                return false;
            }
        }

        // If the user ID matches, the token is valid
        return true;
    }
}