package dev.da0hn.email.management.system.core.ports.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.da0hn.email.management.system.core.domain.Account;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO containing account information for listing and retrieving accounts.
 */
public record AccountOutput(
    UUID id,
    String name,
    String email,
    String host,
    int port,
    String protocol,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime updatedAt,
    int totalRules
) {
    public static AccountOutput of(final Account account) {
        return new AccountOutput(
            account.id(),
            account.name(),
            account.accountCredentials().email(),
            account.emailConnectionDetails().host(),
            account.emailConnectionDetails().port(),
            account.emailConnectionDetails().protocol(),
            account.createdAt(),
            account.updatedAt(),
            account.rules().size()
        );
    }
}
