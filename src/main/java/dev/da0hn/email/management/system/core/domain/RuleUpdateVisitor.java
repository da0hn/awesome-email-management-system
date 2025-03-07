package dev.da0hn.email.management.system.core.domain;

import java.time.LocalDateTime;
import java.util.Set;

public final class RuleUpdateVisitor implements RuleVisitor<Rule> {

    private final RuleUpdateData data;

    private RuleUpdateVisitor(final RuleUpdateData data) {
        this.data = data;
    }

    public static Rule update(
        final Rule rule,
        final String name,
        final String description,
        final Set<RuleCriteria> criteria
    ) {
        final var data = new RuleUpdateData(name, description, criteria, null, null);
        return rule.accept(new RuleUpdateVisitor(data));
    }

    public static Rule update(final Rule rule, final RuleUpdateData data) {
        return rule.accept(new RuleUpdateVisitor(data));
    }

    public static Rule updateMove(
        final Rule rule,
        final String name,
        final String description,
        final Set<RuleCriteria> criteria,
        final String sourceFolder,
        final String targetFolder
    ) {
        final var data = new RuleUpdateData(name, description, criteria, sourceFolder, targetFolder);
        return rule.accept(new RuleUpdateVisitor(data));
    }

    @Override
    public Rule visit(final MoveEmailRule rule) {
        return new MoveEmailRule(
            rule.id(),
            this.data.name(),
            this.data.description(),
            this.data.sourceFolder() != null ? this.data.sourceFolder() : rule.sourceFolder(),
            this.data.targetFolder() != null ? this.data.targetFolder() : rule.targetFolder(),
            this.data.criteria(),
            rule.createdAt(),
            LocalDateTime.now()
        );
    }

    @Override
    public Rule visit(final ArchiveEmailRule rule) {
        return new ArchiveEmailRule(
            rule.id(),
            this.data.name(),
            this.data.description(),
            this.data.criteria(),
            rule.createdAt(),
            LocalDateTime.now()
        );
    }

    @Override
    public Rule visit(final DeleteEmailRule rule) {
        return new DeleteEmailRule(
            rule.id(),
            this.data.name(),
            this.data.description(),
            this.data.criteria(),
            rule.createdAt(),
            LocalDateTime.now()
        );
    }
}
