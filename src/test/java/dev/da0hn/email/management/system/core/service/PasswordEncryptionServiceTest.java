package dev.da0hn.email.management.system.core.service;

import dev.da0hn.email.management.system.core.ports.spi.PasswordEncryption;
import dev.da0hn.email.management.system.infrastructure.security.PasswordEncryptionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordEncryptionServiceTest {

    private PasswordEncryptionService service;
    private PasswordEncryption passwordEncryption;

    @BeforeEach
    void setUp() {
        this.passwordEncryption = new PasswordEncryptionImpl("MySecretKey123");
        this.service = new PasswordEncryptionService(this.passwordEncryption);
    }

    @Test
    @DisplayName("Deve criptografar senha e retornar senha segura")
    void shouldEncryptPasswordAndReturnSecurePassword() {
        final var rawPassword = "password123";

        final var securePassword = this.service.encrypt(rawPassword);

        // Verify the encrypted password is different from original
        assertThat(securePassword.value()).isNotEqualTo(rawPassword);
    }

    @Test
    @DisplayName("Deve descriptografar senha e retornar valor original")
    void shouldDecryptPasswordAndReturnOriginalValue() {
        final var originalPassword = "password123";
        final var securePassword = this.service.encrypt(originalPassword);

        final var decryptedPassword = this.service.decrypt(securePassword.value());

        assertThat(decryptedPassword).isEqualTo(originalPassword);
    }

    @Test
    void shouldThrowExceptionWhenEncryptingNullPassword() {
        assertThatThrownBy(() -> this.service.encrypt(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Password cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenEncryptingEmptyPassword() {
        assertThatThrownBy(() -> this.service.encrypt(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Password cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenDecryptingNullPassword() {
        assertThatThrownBy(() -> this.service.decrypt(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Encrypted password cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenDecryptingEmptyPassword() {
        assertThatThrownBy(() -> this.service.decrypt(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Encrypted password cannot be null or empty");
    }
}
