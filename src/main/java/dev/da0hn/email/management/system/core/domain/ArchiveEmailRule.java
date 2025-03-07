package dev.da0hn.email.management.system.core.domain;

import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@SuperBuilder
public final class ArchiveEmailRule extends Rule {

    @Serial
    private static final long serialVersionUID = 6546901456939289780L;

    protected ArchiveEmailRule(
        final UUID id,
        final String name,
        final String description,
        final Set<RuleCriteria> criteria,
        final LocalDateTime createdAt,
        final LocalDateTime updatedAt
    ) {
        super(id, name, description, RuleAction.ARCHIVE, criteria, createdAt, updatedAt);
    }

    @Override
    public <T> T accept(final RuleVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public static ArchiveEmailRule newRule(
        final UUID id,
        final String name,
        final String description,
        final Set<RuleCriteria> criteria
    ) {
        final var now = LocalDateTime.now();
        return new ArchiveEmailRule(id, name, description, criteria, now, now);
    }

}
