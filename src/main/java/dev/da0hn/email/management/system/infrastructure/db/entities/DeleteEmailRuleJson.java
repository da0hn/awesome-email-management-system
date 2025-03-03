package dev.da0hn.email.management.system.infrastructure.db.entities;

import java.util.List;
import java.util.UUID;

import dev.da0hn.email.management.system.core.domain.RuleAction;
import lombok.Getter;

@Getter
public class DeleteEmailRuleJson extends RuleJson {

    public DeleteEmailRuleJson(final UUID ruleId, final String name, final String description, final List<RuleCriteriaJson> criteria) {
        super(ruleId, name, description, RuleAction.DELETE, criteria);
    }

}
