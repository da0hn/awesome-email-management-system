package dev.da0hn.email.management.system.core.ports.api.validation;

import dev.da0hn.email.management.system.core.domain.RuleAction;
import dev.da0hn.email.management.system.core.ports.api.dto.MoveRuleInput;

/**
 * Interface for DTOs that need rule validation.
 * This interface defines the common fields that are validated by {@link RuleValidator}.
 */
public interface RuleValidatable {
    RuleAction action();
    MoveRuleInput moveRule();
}
