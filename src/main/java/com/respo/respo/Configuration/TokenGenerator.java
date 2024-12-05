package com.respo.respo.Configuration;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

public class TokenGenerator {

    private static final int TOKEN_LENGTH = 16; // Length of the token in bytes
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // Token expires after 24 hours

    /**
     * Generates a secure random token combined with the userId and expiration time.
     *
     * @param userId The user's ID to embed in the token.
     * @return A securely generated token.
     */
    public static String generateResetToken(int userId) {
        // Generate a secure random token
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        new SecureRandom().nextBytes(tokenBytes);

        // Combine user ID and token bytes to create a unique token
        byte[] userIdBytes = String.valueOf(userId).getBytes();
        long expirationTime = System.currentTimeMillis() + EXPIRATION_TIME;  // Expiration timestamp

        // Combine the userId, token, and expiration time into a single byte array
        byte[] expirationBytes = String.valueOf(expirationTime).getBytes();
        byte[] combinedBytes = new byte[userIdBytes.length + tokenBytes.length + expirationBytes.length];
        System.arraycopy(userIdBytes, 0, combinedBytes, 0, userIdBytes.length);
        System.arraycopy(tokenBytes, 0, combinedBytes, userIdBytes.length, tokenBytes.length);
        System.arraycopy(expirationBytes, 0, combinedBytes, userIdBytes.length + tokenBytes.length, expirationBytes.length);

        // Encode the combined bytes using Base64
        return Base64.getEncoder().encodeToString(combinedBytes);
    }

    /**
     * Validates the token by extracting the user ID, token portion, and expiration time.
     *
     * @param token  The token to validate.
     * @param userId The user ID to validate against.
     * @return True if the token is valid and matches the user ID and is not expired, false otherwise.
     */
    public static boolean validateToken(String token, int userId) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(token);
            byte[] userIdBytes = String.valueOf(userId).getBytes();
            byte[] expirationBytes = new byte[decodedBytes.length - userIdBytes.length - TOKEN_LENGTH];
            byte[] tokenBytes = new byte[TOKEN_LENGTH];

            // Extract the token, userId and expiration timestamp from the decoded byte array
            System.arraycopy(decodedBytes, 0, userIdBytes, 0, userIdBytes.length);
            System.arraycopy(decodedBytes, userIdBytes.length, tokenBytes, 0, TOKEN_LENGTH);
            System.arraycopy(decodedBytes, userIdBytes.length + TOKEN_LENGTH, expirationBytes, 0, expirationBytes.length);

            // Validate the user ID portion
            if (!new String(userIdBytes).equals(String.valueOf(userId))) {
                return false;
            }

            // Check the expiration date
            long expirationTime = Long.parseLong(new String(expirationBytes));
            if (System.currentTimeMillis() > expirationTime) {
                return false; // Token is expired
            }

            // If the token is valid and not expired, return true
            return true;
        } catch (Exception e) {
            return false; // If there's any error in decoding or validation, return false
        }
    }
}
