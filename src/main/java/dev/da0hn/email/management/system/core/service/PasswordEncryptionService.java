package dev.da0hn.email.management.system.core.service;

import dev.da0hn.email.management.system.core.domain.SecurePassword;
import dev.da0hn.email.management.system.core.ports.spi.PasswordEncryption;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling password encryption and decryption.
 * This service acts as a facade between the domain layer and the encryption implementation.
 */
@Service
public class PasswordEncryptionService {

    private final PasswordEncryption passwordEncryption;

    public PasswordEncryptionService(PasswordEncryption passwordEncryption) {
        this.passwordEncryption = passwordEncryption;
    }

    /**
     * Encrypts a password and returns it wrapped in a SecurePassword.
     *
     * @param rawPassword the password to encrypt
     * @return a SecurePassword containing the encrypted password
     */
    public SecurePassword encrypt(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        String encrypted = this.passwordEncryption.encrypt(rawPassword);
        return SecurePassword.of(encrypted);
    }

    /**
     * Decrypts a password.
     *
     * @param encryptedPassword the encrypted password to decrypt
     * @return the decrypted password as a String
     */
    public String decrypt(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isEmpty()) {
            throw new IllegalArgumentException("Encrypted password cannot be null or empty");
        }
        return this.passwordEncryption.decrypt(encryptedPassword);
    }
}
