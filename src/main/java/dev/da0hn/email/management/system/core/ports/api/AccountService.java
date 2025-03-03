package dev.da0hn.email.management.system.core.ports.api;

import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;

public interface AccountService {

    void createAccount(NewAccountInput input);

}
