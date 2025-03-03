package dev.da0hn.email.management.system.infrastructure.db.converter.domain;

import dev.da0hn.email.management.system.core.domain.RuleCriteria;
import dev.da0hn.email.management.system.infrastructure.db.entities.RuleCriteriaJson;
import dev.da0hn.email.management.system.shared.annotations.Mapper;
import org.springframework.core.convert.converter.Converter;

@Mapper
public class ToRuleCriteriaConverter implements Converter<RuleCriteriaJson, RuleCriteria> {

    @Override
    public RuleCriteria convert(final RuleCriteriaJson source) {
        return new RuleCriteria(
            source.getCriteriaId(),
            source.getValue(),
            source.getType(),
            source.getOperator()
        );
    }

}
