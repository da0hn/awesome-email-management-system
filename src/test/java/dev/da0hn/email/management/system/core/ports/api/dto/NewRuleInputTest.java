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
    @DisplayName("Deve validar regra de movimentação com configuração válida")
    void shouldValidateMoveRuleWithValidConfiguration() {
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
    @DisplayName("Deve validar regra de movimentação com configuração de movimentação ausente")
    void shouldValidateMoveRuleWithMissingMoveConfiguration() {
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
            .hasFieldOrPropertyWithValue("message", "Configuração de movimentação é obrigatória para ação MOVE");
    }

    @Test
    @DisplayName("Deve validar regra que não é de movimentação com configuração de movimentação")
    void shouldValidateNonMoveRuleWithMoveConfiguration() {
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
            .hasFieldOrPropertyWithValue("message", "Configuração de movimentação é permitida apenas para ação MOVE");
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios")
    void shouldValidateRequiredFields() {
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
                "ID da conta é obrigatório",
                "Nome da regra é obrigatório",
                "Nome da regra deve ter entre 3 e 50 caracteres",
                "Descrição da regra é obrigatória",
                "Ação da regra é obrigatória",
                "Pelo menos um critério é obrigatório"
            );
    }
}
