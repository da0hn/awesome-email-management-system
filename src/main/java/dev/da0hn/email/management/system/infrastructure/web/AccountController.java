package dev.da0hn.email.management.system.infrastructure.web;

import dev.da0hn.email.management.system.core.ports.api.AccountService;
import dev.da0hn.email.management.system.core.ports.api.dto.AccountOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.DetailedAccountOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.UpdateRuleInput;
import dev.da0hn.email.management.system.core.ports.api.dto.UpdateRuleOutput;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

  private final AccountService accountService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public NewAccountOutput create(@RequestBody @Valid final NewAccountInput input) {
    return this.accountService.createAccount(input);
  }

  @PostMapping("/{accountId}/rules")
  @ResponseStatus(HttpStatus.CREATED)
  public NewRuleOutput createRule(
    @PathVariable final UUID accountId,
    @RequestBody @Valid final NewRuleInput input
  ) {
    if (!accountId.equals(input.accountId())) {
      throw new IllegalArgumentException("Account ID in path must match Account ID in request body");
    }
    return this.accountService.createRule(input);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<AccountOutput> findAll() {
    return this.accountService.findAll();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
    public DetailedAccountOutput findById(@PathVariable final UUID id) {
        return this.accountService.findById(id);
    }

    @PutMapping("/{accountId}/rules/{ruleId}")
    @ResponseStatus(HttpStatus.OK)
    public UpdateRuleOutput updateRule(
        @PathVariable final UUID accountId,
        @PathVariable final UUID ruleId,
        @RequestBody @Valid final UpdateRuleInput input
    ) {
        if (!accountId.equals(input.accountId()) || !ruleId.equals(input.ruleId())) {
            throw new IllegalArgumentException("Account ID and Rule ID in path must match IDs in request body");
        }
        return this.accountService.updateRule(input);
    }

    @DeleteMapping("/{accountId}/rules/{ruleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRule(
        @PathVariable final UUID accountId,
        @PathVariable final UUID ruleId
    ) {
        this.accountService.deleteRule(accountId, ruleId);
    }

}
