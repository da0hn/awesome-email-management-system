package dev.da0hn.email.management.system.core.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract sealed class Rule implements Serializable permits ArchiveEmailRule, DeleteEmailRule, MoveEmailRule {

    @Serial
    private static final long serialVersionUID = -2014929154848088461L;

    private final UUID id;

    private final String name;

    private final String description;

    private final RuleAction action;

    protected Rule(final UUID id, final String name, final String description, final RuleAction action) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.action = action;
    }

    public UUID id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public String description() {
        return this.description;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", this.id)
            .append("name", this.name)
            .append("description", this.description)
            .append("action", this.action)
            .toString();
    }

}
