package dev.da0hn.email.management.system.core.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class Account implements Serializable {

    @Serial
    private static final long serialVersionUID = -8147338093223122250L;

    private final Long id;

    private final AccountCredentials accountCredentials;

    private final EmailConnectionDetails emailConnectionDetails;

    private final List<Rule> rules;

}
