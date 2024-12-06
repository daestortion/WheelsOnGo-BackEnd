package com.respo.respo.Configuration;

import java.security.SecureRandom;
import java.util.Base64;

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

        // Get the user ID and expiration time
        byte[] userIdBytes = String.format("%08d", userId).getBytes(); // Format userId to a fixed length
        long expirationTime = System.currentTimeMillis() + EXPIRATION_TIME;

        // Get the expiration time as bytes
        byte[] expirationBytes = String.format("%019d", expirationTime).getBytes(); // Fixed length for expiration time

        // Combine the userId, token, and expiration time into one byte array
        byte[] combinedBytes = new byte[userIdBytes.length + tokenBytes.length + expirationBytes.length];
        System.arraycopy(userIdBytes, 0, combinedBytes, 0, userIdBytes.length);
        System.arraycopy(tokenBytes, 0, combinedBytes, userIdBytes.length, tokenBytes.length);
        System.arraycopy(expirationBytes, 0, combinedBytes, userIdBytes.length + tokenBytes.length, expirationBytes.length);

        // URL-safe encoding using Base64 URL encoder
        return Base64.getUrlEncoder().encodeToString(combinedBytes);
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
            byte[] decodedBytes = Base64.getUrlDecoder().decode(token);

            // Extract user ID, token bytes, and expiration bytes with fixed sizes
            byte[] userIdBytes = new byte[8]; // Adjust this based on your fixed length formatting
            byte[] tokenBytes = new byte[TOKEN_LENGTH];
            byte[] expirationBytes = new byte[19]; // Adjust the length for expiration time

            // Extract parts from the decoded bytes
            System.arraycopy(decodedBytes, 0, userIdBytes, 0, userIdBytes.length);
            System.arraycopy(decodedBytes, userIdBytes.length, tokenBytes, 0, TOKEN_LENGTH);
            System.arraycopy(decodedBytes, userIdBytes.length + TOKEN_LENGTH, expirationBytes, 0, expirationBytes.length);

            // Log for debugging
            System.out.println("Decoded userIdBytes: " + new String(userIdBytes));
            System.out.println("Decoded tokenBytes: " + new String(tokenBytes));
            System.out.println("Decoded expirationBytes: " + new String(expirationBytes));

            // Validate the user ID portion
            if (!new String(userIdBytes).equals(String.format("%08d", userId))) {
                System.out.println("User ID does not match.");
                return false;
            }

            // Check the expiration date
            long expirationTime = Long.parseLong(new String(expirationBytes));
            System.out.println("Expiration Time: " + expirationTime + " Current Time: " + System.currentTimeMillis());
            if (System.currentTimeMillis() > expirationTime) {
                System.out.println("Token expired.");
                return false; // Token is expired
            }

            // If the token is valid and not expired, return true
            return true;
        } catch (Exception e) {
            System.out.println("Error during token validation: " + e.getMessage());
            return false; // If there's any error in decoding or validation, return false
        }
    }
}
