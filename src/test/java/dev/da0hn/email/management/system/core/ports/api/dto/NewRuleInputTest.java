package dev.da0hn.email.management.system.core.ports.api.dto;

import dev.da0hn.email.management.system.core.domain.RuleAction;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaOperator;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NewRuleInputTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("should validate move rule with valid configuration")
    void test1() {
        var input = new NewRuleInput(
            UUID.randomUUID(),
            "Move to Archive",
            "Move emails to archive folder",
            RuleAction.MOVE,
            Set.of(new NewRuleCriteriaInput(
                "test@email.com",
                RuleCriteriaType.FROM,
                RuleCriteriaOperator.EQUALS
            )),
            new MoveRuleInput("INBOX", "ARCHIVE")
        );

        var violations = this.validator.validate(input);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("should validate move rule with missing move configuration")
    void test2() {
        var input = new NewRuleInput(
            UUID.randomUUID(),
            "Move to Archive",
            "Move emails to archive folder",
            RuleAction.MOVE,
            Set.of(new NewRuleCriteriaInput(
                "test@email.com",
                RuleCriteriaType.FROM,
                RuleCriteriaOperator.EQUALS
            )),
            null
        );

        var violations = this.validator.validate(input);

        assertThat(violations)
            .hasSize(1)
            .element(0)
            .hasFieldOrPropertyWithValue("message", "Move rule configuration is required for MOVE action");
    }

    @Test
    @DisplayName("should validate non-move rule with move configuration")
    void test3() {
        var input = new NewRuleInput(
            UUID.randomUUID(),
            "Delete Old Emails",
            "Delete emails older than 30 days",
            RuleAction.DELETE,
            Set.of(new NewRuleCriteriaInput(
                "2024-01-01T00:00:00",
                RuleCriteriaType.RECEIVED_AT,
                RuleCriteriaOperator.GREATER_THAN
            )),
            new MoveRuleInput("INBOX", "ARCHIVE")
        );

        var violations = this.validator.validate(input);

        assertThat(violations)
            .hasSize(1)
            .element(0)
            .hasFieldOrPropertyWithValue("message", "Move rule configuration is only allowed for MOVE action");
    }

    @Test
    @DisplayName("should validate required fields")
    void test4() {
        var input = new NewRuleInput(
            null,
            "",
            "",
            null,
            Set.of(),
            null
        );

        var violations = this.validator.validate(input);

        assertThat(violations)
            .hasSize(6)
            .extracting("message")
            .containsExactlyInAnyOrder(
                "Account ID is required",
                "Rule name is required",
                "Rule name must be between 3 and 50 characters",
                "Rule description is required",
                "Rule action is required",
                "At least one criteria is required"
            );
    }
}
