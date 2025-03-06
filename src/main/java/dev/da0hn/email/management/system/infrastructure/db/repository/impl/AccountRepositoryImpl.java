package dev.da0hn.email.management.system.infrastructure.db.repository.impl;

import dev.da0hn.email.management.system.core.domain.Account;
import dev.da0hn.email.management.system.core.ports.spi.AccountRepository;
import dev.da0hn.email.management.system.infrastructure.db.converter.EntityConverter;
import dev.da0hn.email.management.system.infrastructure.db.entities.AccountEntity;
import dev.da0hn.email.management.system.infrastructure.db.repository.AccountJpaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Repository
@AllArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountJpaRepository accountJpaRepository;

    private final EntityConverter entityConverter;

    @Override
    @Transactional
    public void save(final Account account) {
        final var accountEntity = this.entityConverter.toEntity(account, AccountEntity.class);
        this.accountJpaRepository.save(accountEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findById(final UUID id) {
        return this.accountJpaRepository.findById(id)
            .map(entity -> this.entityConverter.toDomain(entity, Account.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return this.accountJpaRepository.findAll().stream()
            .map(entity -> this.entityConverter.toDomain(entity, Account.class))
            .collect(Collectors.toList());
    }

}
