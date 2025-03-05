package dev.da0hn.email.management.system.core.ports.spi;

/**
 * Port for password encryption operations.
 * This is a secondary/driven port that will be implemented by the infrastructure layer.
 */
public interface PasswordEncryption {

    /**
     * Encrypts a raw password.
     *
     * @param rawPassword the password to encrypt
     * @return the encrypted password
     * @throws IllegalArgumentException if rawPassword is null or empty
     */
    String encrypt(String rawPassword);

    /**
     * Decrypts an encrypted password.
     *
     * @param encryptedPassword the password to decrypt
     * @return the decrypted password
     * @throws IllegalArgumentException if encryptedPassword is null or empty
     */
    String decrypt(String encryptedPassword);

}
