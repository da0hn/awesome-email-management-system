package dev.da0hn.email.management.system.core.ports.api.dto;

import dev.da0hn.email.management.system.core.domain.MoveEmailRule;
import dev.da0hn.email.management.system.core.domain.Rule;
import dev.da0hn.email.management.system.core.domain.RuleAction;
import dev.da0hn.email.management.system.core.domain.RuleCriteria;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO containing detailed rule information including source and target folders for move rules.
 */
public record DetailedRuleOutput(
    UUID id,
    String name,
    String description,
    RuleAction action,
    Set<RuleCriteriaOutput> criteria,
    String sourceFolder,
    String targetFolder
) {
    public static DetailedRuleOutput of(final Rule rule) {
        final String sourceFolder;
        final String targetFolder;
        
        if (rule instanceof MoveEmailRule moveRule) {
            sourceFolder = moveRule.sourceFolder();
            targetFolder = moveRule.targetFolder();
        } else {
            sourceFolder = null;
            targetFolder = null;
        }

        return new DetailedRuleOutput(
            rule.id(),
            rule.name(),
            rule.description(),
            rule.action(),
            rule.criteria().stream()
                .map(DetailedRuleOutput::toCriteriaOutput)
                .collect(Collectors.toSet()),
            sourceFolder,
            targetFolder
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
