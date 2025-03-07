package dev.da0hn.email.management.system.core.ports.api.dto;

import dev.da0hn.email.management.system.core.domain.Rule;
import dev.da0hn.email.management.system.core.domain.RuleAction;
import dev.da0hn.email.management.system.core.domain.RuleCriteria;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UpdateRuleOutput(
    UUID id,
    String name,
    String description,
    RuleAction action,
    Set<RuleCriteriaOutput> criteria,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static UpdateRuleOutput of(final Rule rule) {
        return new UpdateRuleOutput(
            rule.id(),
            rule.name(),
            rule.description(),
            rule.action(),
            rule.criteria().stream()
                .map(UpdateRuleOutput::toCriteriaOutput)
                .collect(Collectors.toSet()),
            rule.createdAt(),
            rule.updatedAt()
        );
    }

    private static RuleCriteriaOutput toCriteriaOutput(final RuleCriteria criteria) {
        return new RuleCriteriaOutput(
            criteria.id(),
            criteria.value(),
            criteria.type(),
            criteria.operator()
        );
    }
}
