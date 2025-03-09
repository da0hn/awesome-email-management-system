package dev.da0hn.email.management.system.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncryptionTest {

    private PasswordEncryptionImpl passwordEncryptionImpl;

    @BeforeEach
    void setUp() {
        this.passwordEncryptionImpl = new PasswordEncryptionImpl("VGhpc0lzVGhlRGVmYXVsdEtleUZvckFFU0VuY3J5cHRpb24=");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "password123",
        "complex!@#$%^&*()password",
        "short",
        "verylongpasswordwithmorethan30characters"
    })
    @DisplayName("Deve criptografar e descriptografar senha")
    void shouldEncryptAndDecryptPassword(String originalPassword) {
        final var encrypted = this.passwordEncryptionImpl.encrypt(originalPassword);
        assertThat(encrypted).isNotEqualTo(originalPassword);

        final var decrypted = this.passwordEncryptionImpl.decrypt(encrypted);
        assertThat(decrypted).isEqualTo(originalPassword);
    }

    @Test
    @DisplayName("Deve gerar o mesmo valor criptografado para a mesma senha")
    void shouldGenerateSameEncryptedValueForSamePassword() {
        final var password = "password123";
        final var encrypted1 = this.passwordEncryptionImpl.encrypt(password);
        final var encrypted2 = this.passwordEncryptionImpl.encrypt(password);

        assertThat(encrypted1).isEqualTo(encrypted2);

        final var decrypted = this.passwordEncryptionImpl.decrypt(encrypted1);
        assertThat(decrypted).isEqualTo(password);
    }

    @Test
    @DisplayName("Deve lançar exceção para senha vazia")
    void shouldThrowExceptionForEmptyPassword() {
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> this.passwordEncryptionImpl.encrypt("")
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para senha nula")
    void shouldThrowExceptionForNullPassword() {
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> this.passwordEncryptionImpl.encrypt(null)
        );

        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> this.passwordEncryptionImpl.decrypt(null)
        );
    }

}
