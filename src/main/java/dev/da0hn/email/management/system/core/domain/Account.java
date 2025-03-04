package dev.da0hn.email.management.system.core.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class Account implements Serializable {

    @Serial
    private static final long serialVersionUID = -8147338093223122250L;

    private final UUID id;

    private final String name;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    private final AccountCredentials accountCredentials;

    private final EmailConnectionDetails emailConnectionDetails;

    private final Set<Rule> rules;

    public static Account newAccount(final NewAccountInput input) {
        return Account.builder()
            .id(UUID.randomUUID())
            .name(input.name())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .accountCredentials(AccountCredentials.builder()
                                    .email(input.credentials().email())
                                    .password(input.credentials().password())
                                    .build())
            .emailConnectionDetails(EmailConnectionDetails.builder()
                                        .host(input.connectionDetails().host())
                                        .port(input.connectionDetails().port())
                                        .protocol(input.connectionDetails().protocol())
                                        .build())
            .rules(new HashSet<>())
            .build();
    }

    public UUID id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public LocalDateTime createdAt() {
        return this.createdAt;
    }

    public LocalDateTime updatedAt() {
        return this.updatedAt;
    }

    public AccountCredentials accountCredentials() {
        return this.accountCredentials;
    }

    public EmailConnectionDetails emailConnectionDetails() {
        return this.emailConnectionDetails;
    }

    public Set<Rule> rules() {
        return Collections.unmodifiableSet(this.rules);
    }

}
