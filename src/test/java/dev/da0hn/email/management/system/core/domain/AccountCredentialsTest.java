package dev.da0hn.email.management.system.core.domain;

import dev.da0hn.email.management.system.core.ports.spi.PasswordEncryption;
import dev.da0hn.email.management.system.infrastructure.security.PasswordEncryptionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountCredentialsTest {

    @Test
    @DisplayName("Deve criar credenciais de conta com senha criptografada")
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
    @DisplayName("Deve lançar exceção quando o email é nulo")
    void shouldThrowExceptionWhenEmailIsNull() {
        assertThatThrownBy(() -> AccountCredentials.builder()
            .email(null)
            .password("encrypted_password")
            .build())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Email cannot be null or blank");
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha é nula")
    void shouldThrowExceptionWhenPasswordIsNull() {
        assertThatThrownBy(() -> AccountCredentials.builder()
            .email("test@example.com")
            .password(null)
            .build())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Encrypted password cannot be null or empty");
    }

    @Test
    @DisplayName("Não deve expor a senha no método toString")
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
