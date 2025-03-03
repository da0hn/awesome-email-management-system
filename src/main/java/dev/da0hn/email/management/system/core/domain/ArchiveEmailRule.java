package dev.da0hn.email.management.system.core.domain;

import java.io.Serial;
import java.util.UUID;

public final class ArchiveEmailRule extends Rule {

    @Serial
    private static final long serialVersionUID = 6546901456939289780L;

    private ArchiveEmailRule(final UUID id, final String name, final String description) {
        super(id, name, description, RuleAction.ARCHIVE);
    }

}
