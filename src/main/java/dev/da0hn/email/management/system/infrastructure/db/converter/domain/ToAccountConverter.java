package dev.da0hn.email.management.system.infrastructure.db.converter.domain;

import java.util.stream.Collectors;

import dev.da0hn.email.management.system.core.domain.Account;
import dev.da0hn.email.management.system.core.domain.AccountCredentials;
import dev.da0hn.email.management.system.core.domain.EmailConnectionDetails;
import dev.da0hn.email.management.system.infrastructure.db.entities.AccountEntity;
import dev.da0hn.email.management.system.shared.annotations.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;

@Mapper
@AllArgsConstructor
public class ToAccountConverter implements Converter<AccountEntity, Account> {

    private final ToRuleConverter toRuleConverter;

    @Override
    public Account convert(final AccountEntity source) {
        return Account.builder()
            .id(source.getId())
            .name(source.getName())
            .accountCredentials(
                AccountCredentials.builder()
                    .email(source.getEmail())
                    .password(source.getPassword())
                    .build()
            )
            .emailConnectionDetails(
                EmailConnectionDetails.builder()
                    .host(source.getHost())
                    .port(source.getPort())
                    .protocol(source.getProtocol())
                    .build()
            )
            .rules(
                source.getRules().stream()
                    .map(this.toRuleConverter::convert)
                    .collect(Collectors.toSet())
            )
            .build();
    }

}
