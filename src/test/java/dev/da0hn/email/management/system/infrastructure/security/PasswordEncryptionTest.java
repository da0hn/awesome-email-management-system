package dev.da0hn.email.management.system.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
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
    void shouldEncryptAndDecryptPassword(String originalPassword) {
        final var encrypted = this.passwordEncryptionImpl.encrypt(originalPassword);
        assertThat(encrypted).isNotEqualTo(originalPassword);

        final var decrypted = this.passwordEncryptionImpl.decrypt(encrypted);
        assertThat(decrypted).isEqualTo(originalPassword);
    }

    @Test
    void shouldGenerateSameEncryptedValueForSamePassword() {
        final var password = "password123";
        final var encrypted1 = this.passwordEncryptionImpl.encrypt(password);
        final var encrypted2 = this.passwordEncryptionImpl.encrypt(password);

        assertThat(encrypted1).isEqualTo(encrypted2);

        final var decrypted = this.passwordEncryptionImpl.decrypt(encrypted1);
        assertThat(decrypted).isEqualTo(password);
    }

    @Test
    void shouldThrowExceptionForEmptyPassword() {
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> this.passwordEncryptionImpl.encrypt("")
        );
    }

    @Test
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
