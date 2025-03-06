package dev.da0hn.email.management.system.core.ports.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.da0hn.email.management.system.core.domain.Account;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO containing detailed account information including rules and criteria for findById endpoint.
 */
public record DetailedAccountOutput(
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
    Set<DetailedRuleOutput> rules
) {
    public static DetailedAccountOutput of(final Account account) {
        return new DetailedAccountOutput(
            account.id(),
            account.name(),
            account.accountCredentials().email(),
            account.emailConnectionDetails().host(),
            account.emailConnectionDetails().port(),
            account.emailConnectionDetails().protocol(),
            account.createdAt(),
            account.updatedAt(),
            account.rules().stream()
                .map(DetailedRuleOutput::of)
                .collect(Collectors.toSet())
        );
    }
}
