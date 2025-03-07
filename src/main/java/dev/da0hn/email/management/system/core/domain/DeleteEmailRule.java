package dev.da0hn.email.management.system.core.domain;

import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@SuperBuilder
public final class DeleteEmailRule extends Rule {

    @Serial
    private static final long serialVersionUID = -1165484820174694626L;

    protected DeleteEmailRule(
        final UUID id,
        final String name,
        final String description,
        final Set<RuleCriteria> criteria,
        final LocalDateTime createdAt,
        final LocalDateTime updatedAt
    ) {
        super(id, name, description, RuleAction.DELETE, criteria, createdAt, updatedAt);
    }

    @Override
    public <T> T accept(final RuleVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public static DeleteEmailRule newRule(
        final UUID id,
        final String name,
        final String description,
        final Set<RuleCriteria> criteria
    ) {
        final var now = LocalDateTime.now();
        return new DeleteEmailRule(id, name, description, criteria, now, now);
    }

}
