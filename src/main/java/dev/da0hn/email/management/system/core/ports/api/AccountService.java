package dev.da0hn.email.management.system.core.ports.api;

import dev.da0hn.email.management.system.core.ports.api.dto.AccountOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.DetailedAccountOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.UpdateRuleInput;
import dev.da0hn.email.management.system.core.ports.api.dto.UpdateRuleOutput;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    NewAccountOutput createAccount(NewAccountInput input);

    NewRuleOutput createRule(NewRuleInput input);

    List<AccountOutput> findAll();

    DetailedAccountOutput findById(UUID id);

    UpdateRuleOutput updateRule(UpdateRuleInput input);

}
