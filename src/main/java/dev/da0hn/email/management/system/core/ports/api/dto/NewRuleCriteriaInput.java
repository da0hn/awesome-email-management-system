package dev.da0hn.email.management.system.core.ports.api.dto;

import dev.da0hn.email.management.system.core.domain.RuleCriteriaOperator;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewRuleCriteriaInput(
    @NotBlank(message = "Criteria value is required")
    String value,
    @NotNull(message = "Criteria type is required")
    RuleCriteriaType type,
    @NotNull(message = "Criteria operator is required")
    RuleCriteriaOperator operator
) {}
