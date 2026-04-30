package com.project.coursework2.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for password hashing.
 * Passwords are stored and compared as lowercase hex-encoded SHA-256 digests.
 */
public class PasswordUtils {

    private PasswordUtils() {}

    /**
     * Hashes a plaintext password using SHA-256.
     *
     * @param password the plaintext password to hash
     * @return lowercase hex string of the SHA-256 digest
     * @throws RuntimeException if SHA-256 is not available on this JVM (should never happen)
     */
    public static String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
