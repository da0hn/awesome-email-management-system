package dev.da0hn.email.management.system.core.service;

import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;
import dev.da0hn.email.management.system.core.ports.spi.AccountRepository;
import dev.da0hn.email.management.system.core.ports.spi.PasswordEncryption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceImplTest {

    private AccountServiceImpl accountService;
    private AccountRepository accountRepository;
    private PasswordEncryptionService passwordEncryptionService;
    private PasswordEncryption passwordEncryption;

    @BeforeEach
    void setUp() {
        this.accountRepository = mock(AccountRepository.class);
        this.passwordEncryption = mock(PasswordEncryption.class);
        this.passwordEncryptionService = new PasswordEncryptionService(this.passwordEncryption);
        this.accountService = new AccountServiceImpl(accountRepository, passwordEncryptionService);
    }

    @Test
    void shouldCreateAccountAndSaveIt() {
        final var rawPassword = "password123";
        final var input = new NewAccountInput(
            "John Doe",
            new NewAccountInput.Credentials("john@example.com", rawPassword),
            new NewAccountInput.ConnectionDetails("smtp.example.com", 587, "smtp")
        );

        when(passwordEncryption.encrypt(rawPassword)).thenReturn("encrypted_password");

        this.accountService.createAccount(input);

        verify(accountRepository).save(argThat(account -> {
            assertThat(account.name()).isEqualTo(input.name());
            assertThat(account.accountCredentials().email()).isEqualTo(input.credentials().email());
            assertThat(account.accountCredentials().password()).isEqualTo("encrypted_password");
            assertThat(account.emailConnectionDetails().host()).isEqualTo(input.connectionDetails().host());
            assertThat(account.emailConnectionDetails().port()).isEqualTo(input.connectionDetails().port());
            assertThat(account.emailConnectionDetails().protocol()).isEqualTo(input.connectionDetails().protocol());
            return true;
        }));
    }

}
