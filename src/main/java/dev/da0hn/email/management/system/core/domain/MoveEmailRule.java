package dev.da0hn.email.management.system.core.domain;

import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@SuperBuilder
public final class MoveEmailRule extends Rule {

    @Serial
    private static final long serialVersionUID = -923350513661019006L;

    private final String sourceFolder;

    private final String targetFolder;

    protected MoveEmailRule(
        final UUID id,
        final String name,
        final String description,
        final String sourceFolder,
        final String targetFolder,
        final Set<RuleCriteria> criteria,
        final LocalDateTime createdAt,
        final LocalDateTime updatedAt
    ) {
        super(id, name, description, RuleAction.MOVE, criteria, createdAt, updatedAt);
        this.sourceFolder = sourceFolder;
        this.targetFolder = targetFolder;
    }

    public String sourceFolder() {
        return this.sourceFolder;
    }

    public String targetFolder() {
        return this.targetFolder;
    }

    @Override
    public <T> T accept(final RuleVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("sourceFolder", this.sourceFolder)
            .append("targetFolder", this.targetFolder)
            .toString();
    }

    public static MoveEmailRule newRule(
        final UUID id,
        final String name,
        final String description,
        final String sourceFolder,
        final String targetFolder,
        final Set<RuleCriteria> criteria
    ) {
        final var now = LocalDateTime.now();
        return new MoveEmailRule(id, name, description, sourceFolder, targetFolder, criteria, now, now);
    }

    public static MoveEmailRule updateRule(
        final UUID id,
        final String name,
        final String description,
        final String sourceFolder,
        final String targetFolder,
        final Set<RuleCriteria> criteria,
        final LocalDateTime createdAt
    ) {
        return new MoveEmailRule(id, name, description, sourceFolder, targetFolder, criteria, createdAt, LocalDateTime.now());
    }

}
