package dev.da0hn.email.management.system.core.domain;

import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuperBuilder
public abstract sealed class Rule implements Serializable permits ArchiveEmailRule, DeleteEmailRule, MoveEmailRule {

    @Serial
    private static final long serialVersionUID = -2014929154848088461L;

    public abstract <T> T accept(RuleVisitor<T> visitor);

    private final UUID id;

    private final String name;

    private final String description;

    private final RuleAction action;

    private final Set<RuleCriteria> criteria;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    protected Rule(
        final UUID id,
        final String name,
        final String description,
        final RuleAction action,
        final Set<RuleCriteria> criteria,
        final LocalDateTime createdAt,
        final LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.action = action;
        this.criteria = new HashSet<>(criteria);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID id() {
        return this.id;
    }

    public final Rule update(final RuleUpdateData data) {
        return RuleUpdateVisitor.update(this, data);
    }

    public String name() {
        return this.name;
    }

    public String description() {
        return this.description;
    }

    public RuleAction action() {
        return this.action;
    }

    public Set<RuleCriteria> criteria() {
        return Collections.unmodifiableSet(this.criteria);
    }

    public LocalDateTime createdAt() {
        return this.createdAt;
    }

    public LocalDateTime updatedAt() {
        return this.updatedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", this.id)
            .append("name", this.name)
            .append("description", this.description)
            .append("action", this.action)
            .append("createdAt", this.createdAt)
            .append("updatedAt", this.updatedAt)
            .toString();
    }

}
