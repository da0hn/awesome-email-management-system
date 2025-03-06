package dev.da0hn.email.management.system.core.service;

import dev.da0hn.email.management.system.core.domain.Account;
import dev.da0hn.email.management.system.core.domain.ArchiveEmailRule;
import dev.da0hn.email.management.system.core.domain.DeleteEmailRule;
import dev.da0hn.email.management.system.core.domain.MoveEmailRule;
import dev.da0hn.email.management.system.core.domain.Rule;
import dev.da0hn.email.management.system.core.domain.RuleAction;
import dev.da0hn.email.management.system.core.domain.RuleCriteria;
import dev.da0hn.email.management.system.core.ports.api.AccountService;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleOutput;
import dev.da0hn.email.management.system.core.ports.spi.AccountRepository;
import dev.da0hn.email.management.system.core.ports.spi.LoggerFacade;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncryptionService passwordEncryptionService;

    @Override
    public NewAccountOutput createAccount(final NewAccountInput input) {
        LoggerFacade.instance()
            .where(this)
            .method("createAccount")
            .what("Creating new account")
            .parameter("input", input)
            .log();

        final var encryptedPassword = this.passwordEncryptionService.encrypt(input.credentials().password());

        final var account = Account.newAccount(input, encryptedPassword);

        this.accountRepository.save(account);

        return NewAccountOutput.of(
            account.id(),
            account.name(),
            account.accountCredentials().email(),
            account.createdAt(),
            account.updatedAt(),
            account.rules().size()
        );
    }

    @Override
    public NewRuleOutput createRule(final NewRuleInput input) {
        LoggerFacade.instance()
            .where(this)
            .method("createRule")
            .what("Creating new rule")
            .parameter("input", input)
            .log();

        final var account = this.accountRepository.findById(input.accountId())
            .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        final var criteria = input.criteria().stream()
            .map(criteriaInput -> new RuleCriteria(
                UUID.randomUUID(),
                criteriaInput.value(),
                criteriaInput.type(),
                criteriaInput.operator()
            ))
            .collect(Collectors.toSet());

        final var rule = createRuleByAction(
            UUID.randomUUID(),
            input.name(),
            input.description(),
            input.action(),
            criteria,
            input
        );

        final var updatedRules = new HashSet<Rule>(account.rules());
        updatedRules.add(rule);
        final var updatedAccount = Account.builder()
            .id(account.id())
            .name(account.name())
            .createdAt(account.createdAt())
            .updatedAt(account.updatedAt())
            .accountCredentials(account.accountCredentials())
            .emailConnectionDetails(account.emailConnectionDetails())
            .rules(updatedRules)
            .build();
        this.accountRepository.save(updatedAccount);

        return NewRuleOutput.of(rule);
    }

    private Rule createRuleByAction(
        final UUID id,
        final String name,
        final String description,
        final RuleAction action,
        final Set<RuleCriteria> criteria,
        final NewRuleInput input
    ) {
        return switch (action) {
            case ARCHIVE -> ArchiveEmailRule.newRule(id, name, description, criteria);
            case DELETE -> DeleteEmailRule.newRule(id, name, description, criteria);
            case MOVE -> MoveEmailRule.newRule(id, name, description, input.moveRule().sourceFolder(), input.moveRule().targetFolder(), criteria);
        };
    }

}
