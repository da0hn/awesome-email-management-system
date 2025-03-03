package dev.da0hn.email.management.system.infrastructure.db.converter.domain;

import java.util.Set;
import java.util.stream.Collectors;

import dev.da0hn.email.management.system.core.domain.ArchiveEmailRule;
import dev.da0hn.email.management.system.core.domain.DeleteEmailRule;
import dev.da0hn.email.management.system.core.domain.MoveEmailRule;
import dev.da0hn.email.management.system.core.domain.Rule;
import dev.da0hn.email.management.system.core.domain.RuleCriteria;
import dev.da0hn.email.management.system.infrastructure.db.entities.MoveEmailRuleJson;
import dev.da0hn.email.management.system.infrastructure.db.entities.RuleJson;
import dev.da0hn.email.management.system.shared.annotations.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;

@Mapper
@AllArgsConstructor
public class ToRuleConverter implements Converter<RuleJson, Rule> {

    private final ToRuleCriteriaConverter toRuleCriteriaConverter;

    @Override
    public Rule convert(final RuleJson source) {
        return switch (source.getAction()) {
            case MOVE -> MoveEmailRule.builder()
                .id(source.getRuleId())
                .action(source.getAction())
                .name(source.getName())
                .description(source.getDescription())
                .sourceFolder(((MoveEmailRuleJson) source).getSourceFolder())
                .targetFolder(((MoveEmailRuleJson) source).getTargetFolder())
                .criteria(this.convertCriteria(source))
                .build();
            case DELETE -> DeleteEmailRule.builder()
                .id(source.getRuleId())
                .action(source.getAction())
                .name(source.getName())
                .description(source.getDescription())
                .criteria(this.convertCriteria(source))
                .build();
            case ARCHIVE -> ArchiveEmailRule.builder()
                .id(source.getRuleId())
                .action(source.getAction())
                .name(source.getName())
                .description(source.getDescription())
                .criteria(this.convertCriteria(source))
                .build();
        };
    }

    private Set<RuleCriteria> convertCriteria(final RuleJson source) {
        return source.getCriteria().stream()
            .map(this.toRuleCriteriaConverter::convert)
            .collect(Collectors.toSet());
    }

}
