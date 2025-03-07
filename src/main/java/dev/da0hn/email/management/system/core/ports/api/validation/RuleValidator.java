package dev.da0hn.email.management.system.core.ports.api.validation;

import dev.da0hn.email.management.system.core.domain.RuleAction;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RuleValidator implements ConstraintValidator<ValidRule, RuleValidatable> {

    @Override
    public void initialize(ValidRule constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(RuleValidatable input, ConstraintValidatorContext context) {
        if (input == null) {
            return true; // Let @NotNull handle null validation
        }

        boolean isValid = true;

        if (input.action() == RuleAction.MOVE && input.moveRule() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Configuração de movimentação é obrigatória para ação MOVE")
                .addConstraintViolation();
            isValid = false;
        }

        if (input.action() != RuleAction.MOVE && input.moveRule() != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Configuração de movimentação é permitida apenas para ação MOVE")
                .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}
