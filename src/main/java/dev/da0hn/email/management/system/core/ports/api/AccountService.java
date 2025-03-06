package dev.da0hn.email.management.system.core.ports.api;

import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleOutput;

public interface AccountService {

    NewAccountOutput createAccount(NewAccountInput input);

    NewRuleOutput createRule(NewRuleInput input);

}
