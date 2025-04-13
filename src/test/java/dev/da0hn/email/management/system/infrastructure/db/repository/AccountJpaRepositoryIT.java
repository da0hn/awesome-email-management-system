package dev.da0hn.email.management.system.infrastructure.db.repository;

import dev.da0hn.email.management.system.infrastructure.db.PostgreSQLPerClassTestcontainers;
import dev.da0hn.email.management.system.infrastructure.db.entities.AccountEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@PostgreSQLPerClassTestcontainers
class AccountJpaRepositoryIT {

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    private AccountEntity createTestAccountEntity() {
        return AccountEntity.builder()
            .id(UUID.randomUUID())
            .name("Test Account")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .email("test@example.com")
            .password("encrypted_password")
            .host("smtp.example.com")
            .port(587)
            .protocol("smtp")
            .rules(new ArrayList<>())
            .build();
    }

    @BeforeEach
    void setUp() {
        this.accountJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve salvar AccountEntity com sucesso")
    void shouldSaveAccountEntitySuccessfully() {
        final var accountEntity = this.createTestAccountEntity();

        final var savedEntity = this.accountJpaRepository.save(accountEntity);

        Assertions.assertThat(savedEntity).isNotNull();
        Assertions.assertThat(savedEntity.getId()).isEqualTo(accountEntity.getId());
        Assertions.assertThat(savedEntity.getName()).isEqualTo(accountEntity.getName());
        Assertions.assertThat(savedEntity.getEmail()).isEqualTo(accountEntity.getEmail());
        Assertions.assertThat(savedEntity.getPassword()).isEqualTo(accountEntity.getPassword());
        Assertions.assertThat(savedEntity.getHost()).isEqualTo(accountEntity.getHost());
        Assertions.assertThat(savedEntity.getPort()).isEqualTo(accountEntity.getPort());
        Assertions.assertThat(savedEntity.getProtocol()).isEqualTo(accountEntity.getProtocol());
        Assertions.assertThat(savedEntity.getRules()).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar AccountEntity por ID")
    void shouldFindAccountEntityById() {
        final var accountEntity = this.createTestAccountEntity();
        this.accountJpaRepository.save(accountEntity);

        final Optional<AccountEntity> result = this.accountJpaRepository.findById(accountEntity.getId());

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getId()).isEqualTo(accountEntity.getId());
    }

    @Test
    @DisplayName("Deve retornar vazio quando AccountEntity não encontrado")
    void shouldReturnEmptyWhenAccountEntityNotFound() {
        final var nonExistentId = UUID.randomUUID();

        final Optional<AccountEntity> result = this.accountJpaRepository.findById(nonExistentId);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar uma lista de AccountEntity existentes")
    void shouldFindAllAccountEntities() {
        final var accountEntity1 = this.createTestAccountEntity();
        final var accountEntity2 = this.createTestAccountEntity();
        this.accountJpaRepository.saveAll(List.of(accountEntity1, accountEntity2));

        final List<AccountEntity> accounts = this.accountJpaRepository.findAll();

        Assertions.assertThat(accounts).hasSize(2);
        Assertions.assertThat(accounts).anyMatch(account -> account.getId().equals(accountEntity1.getId()));
        Assertions.assertThat(accounts).anyMatch(account -> account.getId().equals(accountEntity2.getId()));
    }

}
