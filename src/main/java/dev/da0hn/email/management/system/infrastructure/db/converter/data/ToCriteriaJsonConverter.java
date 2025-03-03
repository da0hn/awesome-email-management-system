package dev.da0hn.email.management.system.infrastructure.db.converter.data;

import dev.da0hn.email.management.system.core.domain.RuleCriteria;
import dev.da0hn.email.management.system.infrastructure.db.entities.RuleCriteriaJson;
import dev.da0hn.email.management.system.shared.annotations.Mapper;
import org.springframework.core.convert.converter.Converter;

@Mapper
public class ToCriteriaJsonConverter implements Converter<RuleCriteria, RuleCriteriaJson> {

    @Override
    public RuleCriteriaJson convert(final RuleCriteria source) {
        return RuleCriteriaJson.builder()
            .criteriaId(source.id())
            .value(source.value())
            .type(source.type())
            .operator(source.operator())
            .build();
    }

}
