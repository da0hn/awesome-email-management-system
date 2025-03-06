package dev.da0hn.email.management.system.core.domain;

import java.io.Serial;
import java.util.Set;
import java.util.UUID;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public final class DeleteEmailRule extends Rule {

    @Serial
    private static final long serialVersionUID = -1165484820174694626L;

    private DeleteEmailRule(final UUID id, final String name, final String description, final Set<RuleCriteria> criteria) {
        super(id, name, description, RuleAction.DELETE, criteria);
    }

    public static DeleteEmailRule newRule(final UUID id, final String name, final String description, final Set<RuleCriteria> criteria) {
        return new DeleteEmailRule(id, name, description, criteria);
    }

}
