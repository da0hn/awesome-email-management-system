package dev.da0hn.email.management.system.infrastructure.db.repository.impl;

import dev.da0hn.email.management.system.core.domain.Account;
import dev.da0hn.email.management.system.core.domain.AccountCredentials;
import dev.da0hn.email.management.system.core.domain.EmailConnectionDetails;
import dev.da0hn.email.management.system.core.ports.spi.AccountRepository;
import dev.da0hn.email.management.system.infrastructure.db.config.PostgresTestContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class AccountRepositoryImplIT extends PostgresTestContainer {

    @Autowired
    private AccountRepository accountRepository;

    private Account createTestAccount() {
        return Account.builder()
            .id(UUID.randomUUID())
            .name("Test Account")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .accountCredentials(
                AccountCredentials.builder()
                    .email("test@example.com")
                    .password("encrypted_password")
                    .build()
            )
            .emailConnectionDetails(
                EmailConnectionDetails.builder()
                    .host("smtp.example.com")
                    .port(587)
                    .protocol("smtp")
                    .build()
            )
            .rules(new HashSet<>())
            .build();
    }

    @Test
    @DisplayName("Should save account successfully")
    void shouldSaveAccountSuccessfully() {

        final var account = this.createTestAccount();

        this.accountRepository.save(account);

        final var savedAccount = this.accountRepository.findById(account.id());
        Assertions.assertThat(savedAccount).isPresent();
        Assertions.assertThat(savedAccount.get().id()).isEqualTo(account.id());
        Assertions.assertThat(savedAccount.get().name()).isEqualTo(account.name());
        Assertions.assertThat(savedAccount.get().accountCredentials().email()).isEqualTo(account.accountCredentials().email());
        Assertions.assertThat(savedAccount.get().accountCredentials().password()).isEqualTo(account.accountCredentials().password());
        Assertions.assertThat(savedAccount.get().emailConnectionDetails().host()).isEqualTo(account.emailConnectionDetails().host());
        Assertions.assertThat(savedAccount.get().emailConnectionDetails().port()).isEqualTo(account.emailConnectionDetails().port());
        Assertions.assertThat(savedAccount.get().emailConnectionDetails().protocol()).isEqualTo(account.emailConnectionDetails().protocol());
    }

    @Test
    @DisplayName("Should find account by id")
    void shouldFindAccountById() {

        final var account = this.createTestAccount();
        this.accountRepository.save(account);

        final Optional<Account> result = this.accountRepository.findById(account.id());

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().id()).isEqualTo(account.id());
    }

    @Test
    @DisplayName("Should return empty when account not found")
    void shouldReturnEmptyWhenAccountNotFound() {

        final var nonExistentId = UUID.randomUUID();

        final Optional<Account> result = this.accountRepository.findById(nonExistentId);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find all accounts")
    void shouldFindAllAccounts() {

        final var account1 = this.createTestAccount();
        final var account2 = this.createTestAccount();
        this.accountRepository.save(account1);
        this.accountRepository.save(account2);

        final List<Account> accounts = this.accountRepository.findAll();

        Assertions.assertThat(accounts).isNotEmpty();
        Assertions.assertThat(accounts).anyMatch(account -> account.id().equals(account1.id()));
        Assertions.assertThat(accounts).anyMatch(account -> account.id().equals(account2.id()));
    }

    @Test
    @DisplayName("Should handle empty rules list correctly")
    void shouldHandleEmptyRulesListCorrectly() {

        final var account = this.createTestAccount();

        this.accountRepository.save(account);

        final var savedAccount = this.accountRepository.findById(account.id());
        Assertions.assertThat(savedAccount).isPresent();
        Assertions.assertThat(savedAccount.get().rules()).isEmpty();
    }

}
