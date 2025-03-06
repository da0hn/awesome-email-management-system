package dev.da0hn.email.management.system.core.ports.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO containing non-sensitive account information returned after account creation.
 */
public record NewAccountOutput(
    UUID id,
    String name,
    String email,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    int totalRules
) {
    public static NewAccountOutput of(
        final UUID id,
        final String name,
        final String email,
        final LocalDateTime createdAt,
        final LocalDateTime updatedAt,
        final int totalRules
    ) {
        return new NewAccountOutput(id, name, email, createdAt, updatedAt, totalRules);
    }
}
