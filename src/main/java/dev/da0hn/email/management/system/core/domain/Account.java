package dev.da0hn.email.management.system.core.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class Account implements Serializable {

    @Serial
    private static final long serialVersionUID = -8147338093223122250L;

    private final UUID id;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    private final AccountCredentials accountCredentials;

    private final EmailConnectionDetails emailConnectionDetails;

    private final List<Rule> rules;

    public UUID id() {
        return this.id;
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

    public List<Rule> rules() {
        return Collections.unmodifiableList(this.rules);
    }

}
