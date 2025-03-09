package dev.da0hn.email.management.system.core.service;

import dev.da0hn.email.management.system.core.domain.Account;
import dev.da0hn.email.management.system.core.domain.AccountCredentials;
import dev.da0hn.email.management.system.core.domain.ArchiveEmailRule;
import dev.da0hn.email.management.system.core.domain.DeleteEmailRule;
import dev.da0hn.email.management.system.core.domain.EmailConnectionDetails;
import dev.da0hn.email.management.system.core.domain.MoveEmailRule;
import dev.da0hn.email.management.system.core.domain.RuleAction;
import dev.da0hn.email.management.system.core.domain.Rule;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaOperator;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaType;
import dev.da0hn.email.management.system.core.ports.api.dto.MoveRuleInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleCriteriaInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleInput;
import dev.da0hn.email.management.system.core.ports.api.dto.UpdateRuleInput;
import dev.da0hn.email.management.system.core.ports.spi.AccountRepository;
import dev.da0hn.email.management.system.core.ports.spi.PasswordEncryption;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceImplTest {

    private AccountServiceImpl accountService;
    private AccountRepository accountRepository;

    private PasswordEncryption passwordEncryption;

    @BeforeEach
    void setUp() {
        this.accountRepository = mock(AccountRepository.class);
        this.passwordEncryption = mock(PasswordEncryption.class);
        final var passwordEncryptionService = new PasswordEncryptionService(this.passwordEncryption);
        this.accountService = new AccountServiceImpl(this.accountRepository, passwordEncryptionService);
    }

    private Account createTestAccount(final UUID accountId) {
        return this.createTestAccount(accountId, null);
    }

    private Account createTestAccount(final UUID accountId, final RuleAction ruleAction) {
        final var rules = new HashSet<Rule>();

        if (ruleAction != null) {
            final var now = LocalDateTime.now();
            final Rule defaultRule = switch (ruleAction) {
                case MOVE -> MoveEmailRule.builder()
                    .id(UUID.randomUUID())
                    .name("Default Rule")
                    .description("Default rule for testing")
                    .action(RuleAction.MOVE)
                    .sourceFolder("INBOX")
                    .targetFolder("ARCHIVE")
                    .criteria(Set.of())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
                case DELETE -> DeleteEmailRule.builder()
                    .id(UUID.randomUUID())
                    .name("Default Rule")
                    .description("Default rule for testing")
                    .action(RuleAction.DELETE)
                    .criteria(Set.of())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
                case ARCHIVE -> ArchiveEmailRule.builder()
                    .id(UUID.randomUUID())
                    .name("Default Rule")
                    .description("Default rule for testing")
                    .action(RuleAction.ARCHIVE)
                    .criteria(Set.of())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            };
            rules.add(defaultRule);
        }

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
            .rules(rules)
            .build();
    }

    @Test
    @DisplayName("Deve criar conta e retornar dados não sensíveis")
    void shouldCreateAccountAndReturnNonSensitiveData() {
        final var rawPassword = "password123";
        final var input = new NewAccountInput(
            "John Doe",
            new NewAccountInput.Credentials("john@example.com", rawPassword),
            new NewAccountInput.ConnectionDetails("smtp.example.com", 587, "smtp")
        );

        when(this.passwordEncryption.encrypt(rawPassword)).thenReturn("encrypted_password");

        final var output = this.accountService.createAccount(input);

        verify(this.accountRepository).save(argThat(account -> {
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
    @DisplayName("Deve criar regra de movimentação com sucesso")
    void shouldCreateMoveRuleSuccessfully() {
        final var accountId = UUID.randomUUID();
        final var account = this.createTestAccount(accountId);

        when(this.accountRepository.findById(accountId)).thenReturn(Optional.of(account));

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

        verify(this.accountRepository).save(argThat(savedAccount -> {
            assertThat(savedAccount.rules()).hasSize(1);
            final var rule = savedAccount.rules().iterator().next();
            assertThat(rule.name()).isEqualTo(input.name());
            assertThat(rule.description()).isEqualTo(input.description());
            assertThat(rule.action()).isEqualTo(input.action());
            return true;
        }));
    }

    @Test
    @DisplayName("Deve criar regra de exclusão com sucesso")
    void shouldCreateDeleteRuleSuccessfully() {
        final var accountId = UUID.randomUUID();
        final var account = this.createTestAccount(accountId);

        when(this.accountRepository.findById(accountId)).thenReturn(Optional.of(account));

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

        verify(this.accountRepository).save(argThat(savedAccount -> {
            assertThat(savedAccount.rules()).hasSize(1);
            final var rule = savedAccount.rules().iterator().next();
            assertThat(rule.name()).isEqualTo(input.name());
            assertThat(rule.description()).isEqualTo(input.description());
            assertThat(rule.action()).isEqualTo(input.action());
            return true;
        }));
    }

    @Test
    @DisplayName("Deve criar regra de arquivamento com sucesso")
    void shouldCreateArchiveRuleSuccessfully() {
        final var accountId = UUID.randomUUID();
        final var account = this.createTestAccount(accountId);

        when(this.accountRepository.findById(accountId)).thenReturn(Optional.of(account));

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

        verify(this.accountRepository).save(argThat(savedAccount -> {
            assertThat(savedAccount.rules()).hasSize(1);
            final var rule = savedAccount.rules().iterator().next();
            assertThat(rule.name()).isEqualTo(input.name());
            assertThat(rule.description()).isEqualTo(input.description());
            assertThat(rule.action()).isEqualTo(input.action());
            return true;
        }));
    }

    @Test
    @DisplayName("Deve atualizar regra de movimentação com sucesso")
    void shouldUpdateMoveRuleSuccessfully() {
        // given
        final var accountId = UUID.randomUUID();
        final var account = this.createTestAccount(accountId, RuleAction.MOVE);
        final var existingRule = account.rules().iterator().next();

        final var input = new UpdateRuleInput(
            existingRule.id(),
            accountId,
            "Updated Rule Name",
            "Updated Rule Description",
            RuleAction.MOVE,
            Set.of(
                new NewRuleCriteriaInput(
                    "updated value",
                    RuleCriteriaType.SUBJECT,
                    RuleCriteriaOperator.CONTAINS
                )
            ),
            new MoveRuleInput(
                "updated/source/folder",
                "updated/target/folder"
            )
        );

        when(this.accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        doAnswer(invocation -> invocation.getArgument(0)).when(this.accountRepository).save(any(Account.class));

        // when
        final var output = this.accountService.updateRule(input);

        // then
        assertThat(output.id()).isEqualTo(existingRule.id());
        assertThat(output.name()).isEqualTo(input.name());
        assertThat(output.description()).isEqualTo(input.description());
        assertThat(output.action()).isEqualTo(input.action());
        assertThat(output.createdAt()).isEqualTo(existingRule.createdAt());
        assertThat(output.updatedAt()).isAfter(existingRule.updatedAt());

        verify(this.accountRepository).save(argThat(updatedAccount ->
            updatedAccount.rules().stream()
                .anyMatch(rule ->
                    rule.id().equals(existingRule.id()) &&
                    rule.name().equals(input.name()) &&
                    rule.description().equals(input.description()) &&
                    rule.action().equals(input.action())
                )
        ));
    }

    @Test
    @DisplayName("Deve atualizar regra de arquivamento com sucesso")
    void shouldUpdateArchiveRuleSuccessfully() {
        // given
        final var accountId = UUID.randomUUID();
        final var account = this.createTestAccount(accountId, RuleAction.ARCHIVE);
        final var existingRule = account.rules().iterator().next();

        final var input = new UpdateRuleInput(
            existingRule.id(),
            accountId,
            "Updated Archive Rule",
            "Updated Archive Rule Description",
            RuleAction.ARCHIVE,
            Set.of(
                new NewRuleCriteriaInput(
                    "updated value",
                    RuleCriteriaType.SUBJECT,
                    RuleCriteriaOperator.CONTAINS
                )
            ),
            null
        );

        when(this.accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        doAnswer(invocation -> invocation.getArgument(0)).when(this.accountRepository).save(any(Account.class));

        // when
        final var output = this.accountService.updateRule(input);

        // then
        assertThat(output.id()).isEqualTo(existingRule.id());
        assertThat(output.name()).isEqualTo(input.name());
        assertThat(output.description()).isEqualTo(input.description());
        assertThat(output.action()).isEqualTo(input.action());
        assertThat(output.createdAt()).isEqualTo(existingRule.createdAt());
        assertThat(output.updatedAt()).isAfter(existingRule.updatedAt());

        verify(this.accountRepository).save(argThat(updatedAccount ->
            updatedAccount.rules().stream()
                .anyMatch(rule ->
                    rule.id().equals(existingRule.id()) &&
                    rule.name().equals(input.name()) &&
                    rule.description().equals(input.description()) &&
                    rule.action().equals(input.action())
                )
        ));
    }

    @Test
    @DisplayName("Deve atualizar regra de exclusão com sucesso")
    void shouldUpdateDeleteRuleSuccessfully() {
        // given
        final var accountId = UUID.randomUUID();
        final var account = this.createTestAccount(accountId, RuleAction.DELETE);
        final var existingRule = account.rules().iterator().next();

        final var input = new UpdateRuleInput(
            existingRule.id(),
            accountId,
            "Updated Delete Rule",
            "Updated Delete Rule Description",
            RuleAction.DELETE,
            Set.of(
                new NewRuleCriteriaInput(
                    "updated value",
                    RuleCriteriaType.SUBJECT,
                    RuleCriteriaOperator.CONTAINS
                )
            ),
            null
        );

        when(this.accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        doAnswer(invocation -> invocation.getArgument(0)).when(this.accountRepository).save(any(Account.class));

        // when
        final var output = this.accountService.updateRule(input);

        // then
        assertThat(output.id()).isEqualTo(existingRule.id());
        assertThat(output.name()).isEqualTo(input.name());
        assertThat(output.description()).isEqualTo(input.description());
        assertThat(output.action()).isEqualTo(input.action());
        assertThat(output.createdAt()).isEqualTo(existingRule.createdAt());
        assertThat(output.updatedAt()).isAfter(existingRule.updatedAt());

        verify(this.accountRepository).save(argThat(updatedAccount ->
            updatedAccount.rules().stream()
                .anyMatch(rule ->
                    rule.id().equals(existingRule.id()) &&
                    rule.name().equals(input.name()) &&
                    rule.description().equals(input.description()) &&
                    rule.action().equals(input.action())
                )
        ));
    }

    @Test
    @DisplayName("Deve lançar exceção quando regra não for encontrada")
    void shouldThrowExceptionWhenRuleNotFound() {
        // given
        final var accountId = UUID.randomUUID();
        final var nonExistentRuleId = UUID.randomUUID();
        final var account = this.createTestAccount(accountId);

        final var input = new UpdateRuleInput(
            nonExistentRuleId,
            accountId,
            "Updated Rule Name",
            "Updated Rule Description",
            RuleAction.MOVE,
            Set.of(
                new NewRuleCriteriaInput(
                    "updated value",
                    RuleCriteriaType.SUBJECT,
                    RuleCriteriaOperator.CONTAINS
                )
            ),
            new MoveRuleInput(
                "updated/source/folder",
                "updated/target/folder"
            )
        );

        when(this.accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // when/then
        assertThatThrownBy(() -> this.accountService.updateRule(input))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Rule not found");
    }

    @Test
    @DisplayName("Deve atualizar regra de movimentação com novas pastas")
    void shouldUpdateMoveRuleWithNewFolders() {
        // given
        final var accountId = UUID.randomUUID();
        final var account = this.createTestAccount(accountId, RuleAction.MOVE);
        final var existingRule = account.rules().iterator().next();

        final var input = new UpdateRuleInput(
            existingRule.id(),
            accountId,
            "Updated Name",
            "Updated Description",
            RuleAction.MOVE,
            Set.of(
                new NewRuleCriteriaInput(
                    "updated value",
                    RuleCriteriaType.SUBJECT,
                    RuleCriteriaOperator.CONTAINS
                )
            ),
            new MoveRuleInput(
                "updated/source",
                "updated/target"
            )
        );

        when(this.accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        doAnswer(invocation -> invocation.getArgument(0)).when(this.accountRepository).save(any(Account.class));

        // when
        final var output = this.accountService.updateRule(input);

        // then
        assertThat(output)
            .satisfies(result -> {
                assertThat(result.id()).isEqualTo(existingRule.id());
                assertThat(result.name()).isEqualTo("Updated Name");
                assertThat(result.description()).isEqualTo("Updated Description");
                assertThat(result.action()).isEqualTo(RuleAction.MOVE);
                assertThat(result.createdAt()).isEqualTo(existingRule.createdAt());
                assertThat(result.updatedAt()).isAfter(existingRule.updatedAt());
                assertThat(result.criteria())
                    .hasSize(1)
                    .allSatisfy(criteria -> {
                        assertThat(criteria.value()).isEqualTo("updated value");
                        assertThat(criteria.type()).isEqualTo(RuleCriteriaType.SUBJECT);
                        assertThat(criteria.operator()).isEqualTo(RuleCriteriaOperator.CONTAINS);
                    });
            });

        verify(this.accountRepository).save(argThat(updatedAccount ->
            updatedAccount.rules().stream()
                .filter(rule -> rule.id().equals(existingRule.id()))
                .findFirst()
                .map(rule -> rule instanceof final MoveEmailRule moveRule &&
                             moveRule.name().equals("Updated Name") &&
                             moveRule.description().equals("Updated Description") &&
                             moveRule.action().equals(RuleAction.MOVE) &&
                             moveRule.sourceFolder().equals("updated/source") &&
                             moveRule.targetFolder().equals("updated/target") &&
                             moveRule.criteria().stream()
                        .allMatch(criteria ->
                            criteria.value().equals("updated value") &&
                            criteria.type().equals(RuleCriteriaType.SUBJECT) &&
                            criteria.operator().equals(RuleCriteriaOperator.CONTAINS)
                        )
                )
                .orElse(false)
        ));
    }

    @Test
    @DisplayName("Deve atualizar regra de movimentação sem alterar pastas")
    void shouldUpdateMoveRuleWithoutChangingFolders() {
        // given
        final var accountId = UUID.randomUUID();
        final var account = this.createTestAccount(accountId, RuleAction.MOVE);
        final var existingRule = account.rules().iterator().next();

        final var input = new UpdateRuleInput(
            existingRule.id(),
            accountId,
            "Updated Name",
            "Updated Description",
            RuleAction.MOVE,
            Set.of(
                new NewRuleCriteriaInput(
                    "updated value",
                    RuleCriteriaType.SUBJECT,
                    RuleCriteriaOperator.CONTAINS
                )
            ),
            null
        );

        when(this.accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        doAnswer(invocation -> invocation.getArgument(0)).when(this.accountRepository).save(any(Account.class));

        // when
        final var output = this.accountService.updateRule(input);

        // then
        assertThat(output)
            .satisfies(result -> {
                assertThat(result.id()).isEqualTo(existingRule.id());
                assertThat(result.name()).isEqualTo("Updated Name");
                assertThat(result.description()).isEqualTo("Updated Description");
                assertThat(result.action()).isEqualTo(RuleAction.MOVE);
                assertThat(result.createdAt()).isEqualTo(existingRule.createdAt());
                assertThat(result.updatedAt()).isAfter(existingRule.updatedAt());
                assertThat(result.criteria())
                    .hasSize(1)
                    .allSatisfy(criteria -> {
                        assertThat(criteria.value()).isEqualTo("updated value");
                        assertThat(criteria.type()).isEqualTo(RuleCriteriaType.SUBJECT);
                        assertThat(criteria.operator()).isEqualTo(RuleCriteriaOperator.CONTAINS);
                    });
            });

        verify(this.accountRepository).save(argThat(updatedAccount ->
            updatedAccount.rules().stream()
                .filter(rule -> rule.id().equals(existingRule.id()))
                .findFirst()
                .map(rule -> rule instanceof final MoveEmailRule moveRule &&
                             moveRule.name().equals("Updated Name") &&
                             moveRule.description().equals("Updated Description") &&
                             moveRule.action().equals(RuleAction.MOVE) &&
                             moveRule.sourceFolder().equals(((MoveEmailRule) existingRule).sourceFolder()) &&
                             moveRule.targetFolder().equals(((MoveEmailRule) existingRule).targetFolder()) &&
                             moveRule.criteria().stream()
                        .allMatch(criteria ->
                            criteria.value().equals("updated value") &&
                            criteria.type().equals(RuleCriteriaType.SUBJECT) &&
                            criteria.operator().equals(RuleCriteriaOperator.CONTAINS)
                        )
                )
                .orElse(false)
        ));
    }

    @Test
    @DisplayName("Deve lançar exceção quando conta não for encontrada")
    void shouldThrowExceptionWhenAccountNotFound() {
        final var accountId = UUID.randomUUID();
        when(this.accountRepository.findById(accountId)).thenReturn(Optional.empty());

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

    @Test
    @DisplayName("Deve remover regra de uma conta com sucesso")
    void shouldDeleteRuleSuccessfully() {
        // given
        final var accountId = UUID.randomUUID();
        final var account = this.createTestAccount(accountId, RuleAction.ARCHIVE);
        final var existingRule = account.rules().iterator().next();

        when(this.accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        doAnswer(invocation -> invocation.getArgument(0)).when(this.accountRepository).save(any(Account.class));

        // when
        this.accountService.deleteRule(accountId, existingRule.id());

        // then
        verify(this.accountRepository).save(argThat(updatedAccount ->
            updatedAccount.rules().stream()
                .noneMatch(rule -> rule.id().equals(existingRule.id()))
        ));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover regra de conta inexistente")
    void shouldThrowExceptionWhenAccountNotFoundOnDeleteRule() {
        // given
        final var accountId = UUID.randomUUID();
        final var ruleId = UUID.randomUUID();

        when(this.accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> this.accountService.deleteRule(accountId, ruleId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Account not found");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover regra inexistente")
    void shouldThrowExceptionWhenRuleNotFoundOnDeleteRule() {
        // given
        final var accountId = UUID.randomUUID();
        final var account = this.createTestAccount(accountId, RuleAction.ARCHIVE);
        final var nonExistingRuleId = UUID.randomUUID();

        when(this.accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // when/then
        assertThatThrownBy(() -> this.accountService.deleteRule(accountId, nonExistingRuleId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Rule not found");
    }
}
