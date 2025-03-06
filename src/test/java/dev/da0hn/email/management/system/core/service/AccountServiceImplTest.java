package dev.da0hn.email.management.system.core.service;

import dev.da0hn.email.management.system.core.domain.Account;
import dev.da0hn.email.management.system.core.domain.AccountCredentials;
import dev.da0hn.email.management.system.core.domain.EmailConnectionDetails;
import dev.da0hn.email.management.system.core.domain.RuleAction;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaOperator;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaType;
import dev.da0hn.email.management.system.core.ports.api.dto.MoveRuleInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleCriteriaInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleInput;
import dev.da0hn.email.management.system.core.ports.spi.AccountRepository;
import dev.da0hn.email.management.system.core.ports.spi.PasswordEncryption;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

    private Account createTestAccount(UUID accountId) {
        return Account.builder()
            .id(accountId)
            .name("John Doe")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .accountCredentials(AccountCredentials.builder()
                .email("john@example.com")
                .password("encrypted_password")
                .build())
            .emailConnectionDetails(EmailConnectionDetails.builder()
                .host("smtp.example.com")
                .port(587)
                .protocol("smtp")
                .build())
            .rules(new HashSet<>())
            .build();
    }

    @Test
    @DisplayName("should create account and return non-sensitive data")
    void shouldCreateAccountAndReturnNonSensitiveData() {
        final var rawPassword = "password123";
        final var input = new NewAccountInput(
            "John Doe",
            new NewAccountInput.Credentials("john@example.com", rawPassword),
            new NewAccountInput.ConnectionDetails("smtp.example.com", 587, "smtp")
        );

        when(passwordEncryption.encrypt(rawPassword)).thenReturn("encrypted_password");

        final var output = this.accountService.createAccount(input);

        verify(accountRepository).save(argThat(account -> {
            assertThat(account.name()).isEqualTo(input.name());
            assertThat(account.accountCredentials().email()).isEqualTo(input.credentials().email());
            assertThat(account.accountCredentials().password()).isEqualTo("encrypted_password");
            assertThat(account.emailConnectionDetails().host()).isEqualTo(input.connectionDetails().host());
            assertThat(account.emailConnectionDetails().port()).isEqualTo(input.connectionDetails().port());
            assertThat(account.emailConnectionDetails().protocol()).isEqualTo(input.connectionDetails().protocol());
            return true;
        }));

        // Verify non-sensitive data is returned
        assertThat(output).isNotNull();
        assertThat(output.id()).isNotNull();
        assertThat(output.name()).isEqualTo(input.name());
        assertThat(output.email()).isEqualTo(input.credentials().email());
        assertThat(output.createdAt()).isNotNull();
        assertThat(output.updatedAt()).isNotNull();
        assertThat(output.totalRules()).isZero();

        // Verify sensitive data is not exposed in the output
        assertThat(output.toString()).doesNotContain(
            rawPassword,
            "encrypted_password",
            input.connectionDetails().host(),
            String.valueOf(input.connectionDetails().port()),
            input.connectionDetails().protocol()
        );
    }

    @Test
    @DisplayName("should create move rule successfully")
    void test1() {
        final var accountId = UUID.randomUUID();
        final var account = createTestAccount(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        final var input = new NewRuleInput(
            accountId,
            "Move to Archive",
            "Move emails to archive folder",
            RuleAction.MOVE,
            Set.of(new NewRuleCriteriaInput(
                "test@email.com",
                RuleCriteriaType.FROM,
                RuleCriteriaOperator.EQUALS
            )),
            new MoveRuleInput("INBOX", "ARCHIVE")
        );

        final var output = this.accountService.createRule(input);

        assertThat(output).isNotNull();
        assertThat(output.id()).isNotNull();
        assertThat(output.name()).isEqualTo(input.name());
        assertThat(output.description()).isEqualTo(input.description());
        assertThat(output.action()).isEqualTo(input.action());
        assertThat(output.criteria()).hasSize(1);

        verify(accountRepository).save(argThat(savedAccount -> {
            assertThat(savedAccount.rules()).hasSize(1);
            final var rule = savedAccount.rules().iterator().next();
            assertThat(rule.name()).isEqualTo(input.name());
            assertThat(rule.description()).isEqualTo(input.description());
            assertThat(rule.action()).isEqualTo(input.action());
            return true;
        }));
    }

    @Test
    @DisplayName("should create delete rule successfully")
    void test2() {
        final var accountId = UUID.randomUUID();
        final var account = createTestAccount(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        final var input = new NewRuleInput(
            accountId,
            "Delete Old Emails",
            "Delete emails older than 30 days",
            RuleAction.DELETE,
            Set.of(new NewRuleCriteriaInput(
                "2024-01-01T00:00:00",
                RuleCriteriaType.RECEIVED_AT,
                RuleCriteriaOperator.GREATER_THAN
            )),
            null
        );

        final var output = this.accountService.createRule(input);

        assertThat(output).isNotNull();
        assertThat(output.id()).isNotNull();
        assertThat(output.name()).isEqualTo(input.name());
        assertThat(output.description()).isEqualTo(input.description());
        assertThat(output.action()).isEqualTo(input.action());
        assertThat(output.criteria()).hasSize(1);

        verify(accountRepository).save(argThat(savedAccount -> {
            assertThat(savedAccount.rules()).hasSize(1);
            final var rule = savedAccount.rules().iterator().next();
            assertThat(rule.name()).isEqualTo(input.name());
            assertThat(rule.description()).isEqualTo(input.description());
            assertThat(rule.action()).isEqualTo(input.action());
            return true;
        }));
    }

    @Test
    @DisplayName("should create archive rule successfully")
    void test3() {
        final var accountId = UUID.randomUUID();
        final var account = createTestAccount(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        final var input = new NewRuleInput(
            accountId,
            "Archive Old Emails",
            "Archive emails older than 30 days",
            RuleAction.ARCHIVE,
            Set.of(new NewRuleCriteriaInput(
                "2024-01-01T00:00:00",
                RuleCriteriaType.RECEIVED_AT,
                RuleCriteriaOperator.GREATER_THAN
            )),
            null
        );

        final var output = this.accountService.createRule(input);

        assertThat(output).isNotNull();
        assertThat(output.id()).isNotNull();
        assertThat(output.name()).isEqualTo(input.name());
        assertThat(output.description()).isEqualTo(input.description());
        assertThat(output.action()).isEqualTo(input.action());
        assertThat(output.criteria()).hasSize(1);

        verify(accountRepository).save(argThat(savedAccount -> {
            assertThat(savedAccount.rules()).hasSize(1);
            final var rule = savedAccount.rules().iterator().next();
            assertThat(rule.name()).isEqualTo(input.name());
            assertThat(rule.description()).isEqualTo(input.description());
            assertThat(rule.action()).isEqualTo(input.action());
            return true;
        }));
    }

    @Test
    @DisplayName("should throw exception when account not found")
    void test4() {
        final var accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        final var input = new NewRuleInput(
            accountId,
            "Move to Archive",
            "Move emails to archive folder",
            RuleAction.MOVE,
            Set.of(new NewRuleCriteriaInput(
                "test@email.com",
                RuleCriteriaType.FROM,
                RuleCriteriaOperator.EQUALS
            )),
            new MoveRuleInput("INBOX", "ARCHIVE")
        );

        assertThatThrownBy(() -> this.accountService.createRule(input))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Account not found");
    }
}
