package dev.da0hn.email.management.system.core.ports.api.validation;

import dev.da0hn.email.management.system.core.domain.RuleAction;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleInput;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RuleValidator implements ConstraintValidator<ValidRule, NewRuleInput> {

    @Override
    public void initialize(ValidRule constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(NewRuleInput input, ConstraintValidatorContext context) {
        if (input == null) {
            return true; // Let @NotNull handle null validation
        }

        boolean isValid = true;

        if (input.action() == RuleAction.MOVE && input.moveRule() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Move rule configuration is required for MOVE action")
                .addConstraintViolation();
            isValid = false;
        }

        if (input.action() != RuleAction.MOVE && input.moveRule() != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Move rule configuration is only allowed for MOVE action")
                .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}
