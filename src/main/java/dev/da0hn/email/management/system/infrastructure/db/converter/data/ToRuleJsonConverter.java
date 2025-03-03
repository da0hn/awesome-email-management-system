package dev.da0hn.email.management.system.infrastructure.db.converter.data;

import dev.da0hn.email.management.system.core.domain.ArchiveEmailRule;
import dev.da0hn.email.management.system.core.domain.DeleteEmailRule;
import dev.da0hn.email.management.system.core.domain.MoveEmailRule;
import dev.da0hn.email.management.system.core.domain.Rule;
import dev.da0hn.email.management.system.infrastructure.db.entities.MoveEmailRuleJson;
import dev.da0hn.email.management.system.infrastructure.db.entities.RuleJson;
import dev.da0hn.email.management.system.shared.annotations.Mapper;
import org.springframework.core.convert.converter.Converter;

@Mapper
public class ToRuleJsonConverter implements Converter<Rule, RuleJson> {

    @Override
    public RuleJson convert(final Rule source) {
        return switch (source) {
            case MoveEmailRule rule -> MoveEmailRuleJson.builder()
                .ruleId(rule.id())
                .description(rule.description())
                .name(rule.name())
                .action(rule.action())
                .sourceFolder(rule.sourceFolder())
                .targetFolder(rule.targetFolder())
                .criteria(rule.criteria().stream()
                              .map()
                              .toList()
                )
                .build();
            case ArchiveEmailRule rule -> null;
            case DeleteEmailRule rule -> null;
        };
    }

}
