package dev.da0hn.email.management.system.core.ports.api.dto;

import dev.da0hn.email.management.system.core.domain.Rule;
import dev.da0hn.email.management.system.core.domain.RuleAction;
import dev.da0hn.email.management.system.core.domain.RuleCriteria;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record NewRuleOutput(
    UUID id,
    String name,
    String description,
    RuleAction action,
    Set<RuleCriteriaOutput> criteria
) {
    public static NewRuleOutput of(final Rule rule) {
        return new NewRuleOutput(
            rule.id(),
            rule.name(),
            rule.description(),
            rule.action(),
            rule.criteria().stream()
                .map(NewRuleOutput::toCriteriaOutput)
                .collect(Collectors.toSet())
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
