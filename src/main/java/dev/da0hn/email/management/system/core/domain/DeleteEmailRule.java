package dev.da0hn.email.management.system.core.domain;

import java.io.Serial;
import java.util.UUID;

public final class DeleteEmailRule extends Rule {

    @Serial
    private static final long serialVersionUID = -1165484820174694626L;

    private DeleteEmailRule(final UUID id, final String name, final String description) {
        super(id, name, description, RuleAction.DELETE);
    }

}
