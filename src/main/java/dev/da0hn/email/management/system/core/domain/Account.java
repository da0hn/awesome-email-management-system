package dev.da0hn.email.management.system.core.domain;

import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Domain entity representing a user account.
 * Use {@link #builder()} to create instances or {@link #newAccount(NewAccountInput, SecurePassword)} for new accounts.
 */
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

    private Account(
        final UUID id,
        final String name,
        final LocalDateTime createdAt,
        final LocalDateTime updatedAt,
        final AccountCredentials accountCredentials,
        final EmailConnectionDetails emailConnectionDetails,
        final Set<Rule> rules
    ) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.accountCredentials = accountCredentials;
        this.emailConnectionDetails = emailConnectionDetails;
        this.rules = rules;
    }

    public static AccountBuilder builder() {
        return new AccountBuilder();
    }

    public static Account newAccount(final NewAccountInput input, final SecurePassword encryptedPassword) {
        if (encryptedPassword == null) {
            throw new IllegalArgumentException("SecurePassword cannot be null");
        }
        return Account.builder()
            .id(UUID.randomUUID())
            .name(input.name())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .accountCredentials(AccountCredentials.builder()
                .email(input.credentials().email())
                .password(encryptedPassword.value())
                .build())
            .emailConnectionDetails(EmailConnectionDetails.builder()
                .host(input.connectionDetails().host())
                .port(input.connectionDetails().port())
                .protocol(input.connectionDetails().protocol())
                .build())
            .rules(new HashSet<>())
            .build();
    }

    public static class AccountBuilder {
        private UUID id;
        private String name;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private AccountCredentials accountCredentials;
        private EmailConnectionDetails emailConnectionDetails;
        private Set<Rule> rules;

        private AccountBuilder() {
            // Private constructor to enforce builder usage
        }

        public AccountBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public AccountBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AccountBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AccountBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AccountBuilder accountCredentials(AccountCredentials accountCredentials) {
            this.accountCredentials = accountCredentials;
            return this;
        }

        public AccountBuilder emailConnectionDetails(EmailConnectionDetails emailConnectionDetails) {
            this.emailConnectionDetails = emailConnectionDetails;
            return this;
        }

        public AccountBuilder rules(Set<Rule> rules) {
            this.rules = rules;
            return this;
        }

        public Account build() {
            validateRequiredFields();
            return new Account(
                id,
                name,
                createdAt,
                updatedAt,
                accountCredentials,
                emailConnectionDetails,
                rules != null ? rules : new HashSet<>()
            );
        }

        private void validateRequiredFields() {
            if (id == null) throw new IllegalArgumentException("Id cannot be null");
            if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be null or blank");
            if (createdAt == null) throw new IllegalArgumentException("CreatedAt cannot be null");
            if (updatedAt == null) throw new IllegalArgumentException("UpdatedAt cannot be null");
            if (accountCredentials == null) throw new IllegalArgumentException("AccountCredentials cannot be null");
            if (emailConnectionDetails == null) throw new IllegalArgumentException("EmailConnectionDetails cannot be null");
        }
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
