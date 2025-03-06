package dev.da0hn.email.management.system.core.ports.api.dto;

import dev.da0hn.email.management.system.core.domain.RuleCriteriaOperator;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaType;

import java.util.UUID;

public record RuleCriteriaOutput(
    UUID id,
    String value,
    RuleCriteriaType type,
    RuleCriteriaOperator operator
) {}
