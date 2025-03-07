package dev.da0hn.email.management.system.core.ports.api.dto;

import dev.da0hn.email.management.system.core.domain.RuleAction;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaOperator;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateRuleInputTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Deve validar um UpdateRuleInput válido")
    void shouldValidateValidUpdateRuleInput() {
        // given
        final var input = new UpdateRuleInput(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Test Rule",
            "Test Description",
            RuleAction.MOVE,
            Set.of(new NewRuleCriteriaInput("test", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS)),
            new MoveRuleInput("source", "target")
        );

        // when
        final var violations = this.validator.validate(input);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Deve validar um UpdateRuleInput com campos obrigatórios nulos")
    void shouldValidateUpdateRuleInputWithNullRequiredFields() {
        // given
        final var input = new UpdateRuleInput(
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        // when
        final var violations = this.validator.validate(input);

        // then
        assertThat(violations)
            .hasSize(6)
            .extracting("message")
            .containsExactlyInAnyOrder(
                "ID da regra é obrigatório",
                "ID da conta é obrigatório",
                "Nome da regra é obrigatório",
                "Descrição da regra é obrigatória",
                "Ação da regra é obrigatória",
                "Pelo menos um critério é obrigatório"
            );
    }

    @Test
    @DisplayName("Deve validar um UpdateRuleInput com nome inválido")
    void shouldValidateUpdateRuleInputWithInvalidName() {
        // given
        final var input = new UpdateRuleInput(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "ab",  // menos que 3 caracteres
            "Test Description",
            RuleAction.MOVE,
            Set.of(new NewRuleCriteriaInput("test", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS)),
            new MoveRuleInput("source", "target")
        );

        // when
        final var violations = this.validator.validate(input);

        // then
        assertThat(violations)
            .hasSize(1)
            .extracting("message")
            .containsExactly("Nome da regra deve ter entre 3 e 50 caracteres");
    }

    @Test
    @DisplayName("Deve validar um UpdateRuleInput com descrição muito longa")
    void shouldValidateUpdateRuleInputWithTooLongDescription() {
        // given
        final var input = new UpdateRuleInput(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Test Rule",
            "a".repeat(256),  // 256 caracteres
            RuleAction.MOVE,
            Set.of(new NewRuleCriteriaInput("test", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS)),
            new MoveRuleInput("source", "target")
        );

        // when
        final var violations = this.validator.validate(input);

        // then
        assertThat(violations)
            .hasSize(1)
            .extracting("message")
            .containsExactly("Descrição da regra não deve exceder 255 caracteres");
    }

    @Test
    @DisplayName("Deve validar um UpdateRuleInput com muitos critérios")
    void shouldValidateUpdateRuleInputWithTooManyCriteria() {
        // given
        final var criteria = Set.of(
            new NewRuleCriteriaInput("test1", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS),
            new NewRuleCriteriaInput("test2", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS),
            new NewRuleCriteriaInput("test3", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS),
            new NewRuleCriteriaInput("test4", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS),
            new NewRuleCriteriaInput("test5", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS),
            new NewRuleCriteriaInput("test6", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS),
            new NewRuleCriteriaInput("test7", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS),
            new NewRuleCriteriaInput("test8", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS),
            new NewRuleCriteriaInput("test9", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS),
            new NewRuleCriteriaInput("test10", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS),
            new NewRuleCriteriaInput("test11", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS)
        );

        final var input = new UpdateRuleInput(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Test Rule",
            "Test Description",
            RuleAction.MOVE,
            criteria,
            new MoveRuleInput("source", "target")
        );

        // when
        final var violations = this.validator.validate(input);

        // then
        assertThat(violations)
            .hasSize(1)
            .extracting("message")
            .containsExactly("Máximo de 10 critérios permitidos");
    }

    @Test
    @DisplayName("Deve validar regra de movimentação com configuração de movimentação ausente")
    void shouldValidateMoveRuleWithMissingMoveConfiguration() {
        // given
        final var input = new UpdateRuleInput(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Move Rule",
            "Move emails to another folder",
            RuleAction.MOVE,
            Set.of(new NewRuleCriteriaInput("test", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS)),
            null
        );

        // when
        final var violations = this.validator.validate(input);

        // then
        assertThat(violations)
            .hasSize(1)
            .element(0)
            .hasFieldOrPropertyWithValue("message", "Configuração de movimentação é obrigatória para ação MOVE");
    }

    @Test
    @DisplayName("Deve validar regra que não é de movimentação com configuração de movimentação")
    void shouldValidateNonMoveRuleWithMoveConfiguration() {
        // given
        final var input = new UpdateRuleInput(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Delete Rule",
            "Delete old emails",
            RuleAction.DELETE,
            Set.of(new NewRuleCriteriaInput("test", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS)),
            new MoveRuleInput("source", "target")
        );

        // when
        final var violations = this.validator.validate(input);

        // then
        assertThat(violations)
            .hasSize(1)
            .element(0)
            .hasFieldOrPropertyWithValue("message", "Configuração de movimentação é permitida apenas para ação MOVE");
    }
}
