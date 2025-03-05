package dev.da0hn.email.management.system.core.domain;

import dev.da0hn.email.management.system.core.ports.spi.PasswordEncryption;
import dev.da0hn.email.management.system.infrastructure.security.PasswordEncryptionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountCredentialsTest {

    @Test
    void shouldCreateAccountCredentialsWithEncryptedPassword() {
        final var email = "test@example.com";
        final var encryptedPassword = "encrypted_password_123";

        final var credentials = AccountCredentials.builder()
            .email(email)
            .password(encryptedPassword)
            .build();

        assertThat(credentials.email()).isEqualTo(email);
        assertThat(credentials.password()).isEqualTo(encryptedPassword);
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        assertThatThrownBy(() -> AccountCredentials.builder()
            .email(null)
            .password("encrypted_password")
            .build())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Email cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        assertThatThrownBy(() -> AccountCredentials.builder()
            .email("test@example.com")
            .password(null)
            .build())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Encrypted password cannot be null or empty");
    }

    @Test
    void shouldNotExposePasswordInToString() {
        final var credentials = AccountCredentials.builder()
            .email("test@example.com")
            .password("encrypted_password_123")
            .build();

        assertThat(credentials.toString())
            .contains("email=test@example.com")
            .contains("password=[PROTECTED]")
            .doesNotContain("encrypted_password_123");
    }

}
