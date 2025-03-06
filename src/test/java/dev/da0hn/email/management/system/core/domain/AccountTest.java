package dev.da0hn.email.management.system.core.domain;

import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;
import dev.da0hn.email.management.system.core.ports.spi.PasswordEncryption;
import dev.da0hn.email.management.system.core.service.PasswordEncryptionService;
import dev.da0hn.email.management.system.infrastructure.security.PasswordEncryptionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    private PasswordEncryptionService passwordEncryptionService;

    @BeforeEach
    void setUp() {
        var passwordEncryption = new PasswordEncryptionImpl("MySecretKey123");
        this.passwordEncryptionService = new PasswordEncryptionService(passwordEncryption);
    }

    @Test
    @DisplayName("Deve criar nova conta com senha criptografada")
    void shouldCreateNewAccountWithEncryptedPassword() {
        final var rawPassword = "password123";
        final var input = new NewAccountInput(
            "John Doe",
            new NewAccountInput.Credentials("john@example.com", rawPassword),
            new NewAccountInput.ConnectionDetails("smtp.example.com", 587, "smtp")
        );

        final var encryptedPassword = this.passwordEncryptionService.encrypt(rawPassword);
        final var account = Account.newAccount(input, encryptedPassword);

        assertThat(account.name()).isEqualTo(input.name());
        assertThat(account.accountCredentials().email()).isEqualTo(input.credentials().email());
        assertThat(account.accountCredentials().password()).isEqualTo(encryptedPassword.value());
        assertThat(account.emailConnectionDetails().host()).isEqualTo(input.connectionDetails().host());
        assertThat(account.emailConnectionDetails().port()).isEqualTo(input.connectionDetails().port());
        assertThat(account.emailConnectionDetails().protocol()).isEqualTo(input.connectionDetails().protocol());
        assertThat(account.rules()).isEmpty();
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha segura é nula")
    void shouldThrowExceptionWhenSecurePasswordIsNull() {
        final var input = new NewAccountInput(
            "John Doe",
            new NewAccountInput.Credentials("john@example.com", "password123"),
            new NewAccountInput.ConnectionDetails("smtp.example.com", 587, "smtp")
        );

        assertThatThrownBy(() -> Account.newAccount(input, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("SecurePassword cannot be null");
    }

}
