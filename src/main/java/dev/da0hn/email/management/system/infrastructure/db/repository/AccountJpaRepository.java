package dev.da0hn.email.management.system.infrastructure.db.repository;

import dev.da0hn.email.management.system.infrastructure.db.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {
}
