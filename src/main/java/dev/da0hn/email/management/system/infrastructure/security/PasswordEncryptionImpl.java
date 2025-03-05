package dev.da0hn.email.management.system.infrastructure.security;

import dev.da0hn.email.management.system.core.ports.spi.PasswordEncryption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Component
public class PasswordEncryptionImpl implements PasswordEncryption {

    private static final String ALGORITHM = "AES";
    private final SecretKey key;

    public PasswordEncryptionImpl(@Value("${app.encryption.key}") String secretKey) {
        try {
            // Generate a 256-bit (32 bytes) key using SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(secretKey.getBytes(StandardCharsets.UTF_8));
            // Use first 16 bytes for AES-128
            byte[] keyBytes = Arrays.copyOf(hash, 16);
            this.key = new SecretKeySpec(keyBytes, ALGORITHM);
        }
        catch (Exception e) {
            throw new RuntimeException("Error initializing encryption key", e);
        }
    }

    public String encrypt(final String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, this.key);
            byte[] passwordBytes = rawPassword.getBytes(StandardCharsets.UTF_8);
            try {
                byte[] encryptedBytes = cipher.doFinal(passwordBytes);
                return Base64.getEncoder().encodeToString(encryptedBytes);
            } finally {
                // Clear the byte array containing the password
                Arrays.fill(passwordBytes, (byte) 0);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error encrypting password", e);
        }
    }

    public String decrypt(final String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isEmpty()) {
            throw new IllegalArgumentException("Encrypted password cannot be null or empty");
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, this.key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
            try {
                return new String(decryptedBytes, StandardCharsets.UTF_8);
            } finally {
                // Clear the byte array containing the decrypted password
                Arrays.fill(decryptedBytes, (byte) 0);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error decrypting password", e);
        }
    }

}
