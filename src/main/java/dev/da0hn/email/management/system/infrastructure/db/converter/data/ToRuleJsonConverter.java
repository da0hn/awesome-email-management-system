package dev.da0hn.email.management.system.infrastructure.db.converter.data;

import dev.da0hn.email.management.system.core.domain.ArchiveEmailRule;
import dev.da0hn.email.management.system.core.domain.DeleteEmailRule;
import dev.da0hn.email.management.system.core.domain.MoveEmailRule;
import dev.da0hn.email.management.system.core.domain.Rule;
import dev.da0hn.email.management.system.infrastructure.db.entities.ArchiveEmailRuleJson;
import dev.da0hn.email.management.system.infrastructure.db.entities.DeleteEmailRuleJson;
import dev.da0hn.email.management.system.infrastructure.db.entities.MoveEmailRuleJson;
import dev.da0hn.email.management.system.infrastructure.db.entities.RuleJson;
import dev.da0hn.email.management.system.shared.annotations.Mapper;
import org.springframework.core.convert.converter.Converter;

@Mapper
public class ToRuleJsonConverter implements Converter<Rule, RuleJson> {

    private final ToCriteriaJsonConverter toCriteriaJsonConverter;

    public ToRuleJsonConverter(final ToCriteriaJsonConverter toCriteriaJsonConverter) {
        this.toCriteriaJsonConverter =
            toCriteriaJsonConverter;
    }

    @Override
    public RuleJson convert(final Rule source) {
        return switch (source) {
            case final MoveEmailRule rule -> MoveEmailRuleJson.builder()
                .ruleId(rule.id())
                .name(rule.name())
                .description(rule.description())
                .action(rule.action())
                .sourceFolder(rule.sourceFolder())
                .targetFolder(rule.targetFolder())
                .criteria(
                    rule.criteria().stream()
                        .map(this.toCriteriaJsonConverter::convert)
                        .toList()
                )
                .build();
            case final ArchiveEmailRule rule -> ArchiveEmailRuleJson.builder()
                .ruleId(rule.id())
                .name(rule.name())
                .description(rule.description())
                .action(rule.action())
                .criteria(
                    rule.criteria().stream()
                        .map(this.toCriteriaJsonConverter::convert)
                        .toList()
                )
                .build();
            case final DeleteEmailRule rule -> DeleteEmailRuleJson.builder()
                .ruleId(rule.id())
                .name(rule.name())
                .description(rule.description())
                .action(rule.action())
                .criteria(
                    rule.criteria().stream()
                        .map(this.toCriteriaJsonConverter::convert)
                        .toList()
                )
                .build();
        };
    }

}
