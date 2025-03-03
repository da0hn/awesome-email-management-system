package dev.da0hn.email.management.system.infrastructure.db.converter.data;

import dev.da0hn.email.management.system.core.domain.Account;
import dev.da0hn.email.management.system.infrastructure.db.entities.AccountEntity;
import dev.da0hn.email.management.system.shared.annotations.Mapper;
import org.springframework.core.convert.converter.Converter;

@Mapper
public class ToAccountEntityConverter implements Converter<Account, AccountEntity> {

    private final ToRuleJsonConverter toRuleJsonConverter;

    public ToAccountEntityConverter(final ToRuleJsonConverter toRuleJsonConverter) { this.toRuleJsonConverter = toRuleJsonConverter; }

    @Override
    public AccountEntity convert(final Account source) {
        return AccountEntity.builder()
            .id(source.id())
            .username(source.accountCredentials().username())
            .password(source.accountCredentials().password())
            .host(source.emailConnectionDetails().host())
            .port(source.emailConnectionDetails().port())
            .protocol(source.emailConnectionDetails().protocol())
            .createdAt(source.createdAt())
            .updatedAt(source.updatedAt())
            .rules(
                source.rules().stream()
                    .map(this.toRuleJsonConverter::convert)
                    .toList()
            )
            .build();
    }

}
