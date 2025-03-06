package dev.da0hn.email.management.system.core.ports.spi;

import dev.da0hn.email.management.system.core.domain.Account;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    @Transactional
    void save(Account account);

    @Transactional(readOnly = true)
    Optional<Account> findById(UUID id);

    @Transactional(readOnly = true)
    List<Account> findAll();

}
