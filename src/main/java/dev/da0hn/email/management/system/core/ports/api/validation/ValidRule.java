package dev.da0hn.email.management.system.core.ports.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RuleValidator.class)
public @interface ValidRule {
    String message() default "Invalid rule configuration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
