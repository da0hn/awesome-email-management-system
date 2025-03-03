package dev.da0hn.email.management.system.core.ports.spi;

import dev.da0hn.email.management.system.core.domain.Account;
import org.springframework.transaction.annotation.Transactional;

public interface AccountRepository {

    @Transactional
    void save(Account account);

}
