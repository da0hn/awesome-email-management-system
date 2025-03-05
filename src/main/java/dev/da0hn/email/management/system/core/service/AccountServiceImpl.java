package dev.da0hn.email.management.system.core.service;

import dev.da0hn.email.management.system.core.domain.Account;
import dev.da0hn.email.management.system.core.ports.api.AccountService;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;
import dev.da0hn.email.management.system.core.ports.spi.AccountRepository;
import dev.da0hn.email.management.system.core.ports.spi.LoggerFacade;
import dev.da0hn.email.management.system.core.ports.spi.PasswordEncryption;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncryptionService passwordEncryptionService;

    @Override
    public void createAccount(final NewAccountInput input) {
        LoggerFacade.instance()
            .where(this)
            .method("createAccount")
            .what("Creating new account")
            .parameter("input", input)
            .log();

        // Encrypt the password before creating the account
        final var encryptedPassword = this.passwordEncryptionService.encrypt(input.credentials().password());
        final var account = Account.newAccount(input, encryptedPassword);

        this.accountRepository.save(account);
    }

}
