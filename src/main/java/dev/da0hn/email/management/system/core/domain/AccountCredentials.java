package dev.da0hn.email.management.system.core.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * Value object representing account credentials with encrypted password.
 * Use {@link #builder()} to create instances.
 */
public class AccountCredentials implements Serializable, SensitiveData {

    @Serial
    private static final long serialVersionUID = -2235376574903307288L;

    private final String email;
    private final SecurePassword password;

    private AccountCredentials(
        final String email,
        final SecurePassword password
    ) {
        this.email = email;
        this.password = password;
    }

    /**
     * Creates a new builder for AccountCredentials.
     *
     * @return a new builder instance
     */
    public static AccountCredentialsBuilder builder() {
        return new AccountCredentialsBuilder();
    }

    /**
     * Builder for creating AccountCredentials instances with proper validation.
     */
    public static final class AccountCredentialsBuilder {
        private String email;
        private SecurePassword password;

        private AccountCredentialsBuilder() {
            // Private constructor to enforce builder usage
        }

        /**
         * Sets the email address.
         *
         * @param email the email address
         * @return this builder
         */
        public AccountCredentialsBuilder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Sets the encrypted password.
         *
         * @param encryptedPassword the encrypted password
         * @return this builder
         */
        public AccountCredentialsBuilder password(String encryptedPassword) {
            this.password = SecurePassword.of(encryptedPassword);
            return this;
        }

        /**
         * Validates and builds a new AccountCredentials instance.
         *
         * @return a new AccountCredentials instance
         * @throws IllegalArgumentException if any required field is invalid
         */
        public AccountCredentials build() {
            validateRequiredFields();
            return new AccountCredentials(email, password);
        }

        private void validateRequiredFields() {
            validateEmail();
            validatePassword();
        }

        private void validateEmail() {
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email cannot be null or blank");
            }
        }

        private void validatePassword() {
            if (password == null) {
                throw new IllegalArgumentException("Encrypted password cannot be null or empty");
            }
        }
    }

    public String email() {
        return this.email;
    }

    public String password() {
        return this.password.value();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("email", this.email)
            .append("password", "[PROTECTED]")
            .toString();
    }

}
