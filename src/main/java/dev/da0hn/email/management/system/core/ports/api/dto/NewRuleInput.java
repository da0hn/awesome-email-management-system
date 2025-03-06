package dev.da0hn.email.management.system.core.ports.api.dto;

import dev.da0hn.email.management.system.core.domain.RuleAction;
import dev.da0hn.email.management.system.core.ports.api.validation.ValidRule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

@ValidRule
public record NewRuleInput(
    @NotNull(message = "Account ID is required")
    UUID accountId,
    @NotBlank(message = "Rule name is required")
    @Size(min = 3, max = 50, message = "Rule name must be between 3 and 50 characters")
    String name,
    @NotBlank(message = "Rule description is required")
    @Size(max = 255, message = "Rule description must not exceed 255 characters")
    String description,
    @NotNull(message = "Rule action is required")
    RuleAction action,
    @NotEmpty(message = "At least one criteria is required")
    @Size(max = 10, message = "Maximum of 10 criteria allowed")
    @Valid
    Set<NewRuleCriteriaInput> criteria,
    @Valid
    MoveRuleInput moveRule
) {}
