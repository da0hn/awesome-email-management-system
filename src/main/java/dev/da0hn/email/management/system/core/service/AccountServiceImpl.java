package dev.da0hn.email.management.system.core.service;

import dev.da0hn.email.management.system.core.domain.Account;
import dev.da0hn.email.management.system.core.ports.api.AccountService;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;
import dev.da0hn.email.management.system.core.ports.spi.AccountRepository;
import dev.da0hn.email.management.system.core.ports.spi.LoggerFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public void createAccount(final NewAccountInput input) {
        LoggerFacade.instance()
            .where(this)
            .method("createAccount")
            .what("Creating new account")
            .parameter("input", input)
            .log();

        final var account = Account.newAccount(input);

        this.accountRepository.save(account);
    }

}
