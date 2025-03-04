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
        if (rawPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, this.key);
            byte[] encryptedBytes = cipher.doFinal(rawPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }
        catch (Exception e) {
            throw new RuntimeException("Error encrypting password", e);
        }
    }

    public String decrypt(final String encryptedPassword) {
        if (encryptedPassword == null) {
            throw new IllegalArgumentException("Encrypted password cannot be null");
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, this.key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            throw new RuntimeException("Error decrypting password", e);
        }
    }

}
