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

}
