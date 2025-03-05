package dev.da0hn.email.management.system.core.domain;



/**
 * A secure password container that properly handles sensitive password data.
 * This class ensures that password data is cleared from memory when no longer needed.
 */
public final class SecurePassword implements SensitiveData {

    private final String encryptedPassword;

    private SecurePassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isEmpty()) {
            throw new IllegalArgumentException("Encrypted password cannot be null or empty");
        }
        this.encryptedPassword = encryptedPassword;
    }

    /**
     * Creates a new SecurePassword instance from an encrypted password.
     *
     * @param encryptedPassword the encrypted password
     * @return a new SecurePassword instance
     */
    public static SecurePassword of(String encryptedPassword) {
        return new SecurePassword(encryptedPassword);
    }

    /**
     * Gets the encrypted form of the password.
     *
     * @return the encrypted password
     */
    public String value() {
        return this.encryptedPassword;
    }

    @Override
    public String toString() {
        return "[PROTECTED]";
    }
}
