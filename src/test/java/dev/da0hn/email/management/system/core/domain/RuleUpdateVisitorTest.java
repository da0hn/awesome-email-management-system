package dev.da0hn.email.management.system.core.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

class RuleUpdateVisitorTest {

    @Test
    @DisplayName("Deve atualizar regra de movimentação com novas pastas")
    void shouldUpdateMoveRuleWithNewFolders() {
        final var id = UUID.randomUUID();
        final var now = LocalDateTime.now();
        final var originalRule = new MoveEmailRule(
            id,
            "Original Name",
            "Original Description",
            "original/source",
            "original/target",
            Set.of(new RuleCriteria(UUID.randomUUID(), "value", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS)),
            now,
            now
        );

        final var updatedRule = RuleUpdateVisitor.updateMove(
            originalRule,
            "Updated Name",
            "Updated Description",
            Set.of(new RuleCriteria(UUID.randomUUID(), "new value", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS)),
            "new/source",
            "new/target"
        );

        Assertions.assertThat(updatedRule)
            .isInstanceOf(MoveEmailRule.class)
            .satisfies(rule -> {
                final var moveRule = (MoveEmailRule) rule;
                Assertions.assertThat(moveRule.id()).isEqualTo(id);
                Assertions.assertThat(moveRule.name()).isEqualTo("Updated Name");
                Assertions.assertThat(moveRule.description()).isEqualTo("Updated Description");
                Assertions.assertThat(moveRule.sourceFolder()).isEqualTo("new/source");
                Assertions.assertThat(moveRule.targetFolder()).isEqualTo("new/target");
                Assertions.assertThat(moveRule.createdAt()).isEqualTo(now);
                Assertions.assertThat(moveRule.updatedAt()).isAfterOrEqualTo(now);
            });
    }

    @Test
    @DisplayName("Deve atualizar regra de movimentação mantendo pastas originais")
    void shouldUpdateMoveRuleKeepingOriginalFolders() {
        final var id = UUID.randomUUID();
        final var now = LocalDateTime.now();
        final var originalRule = new MoveEmailRule(
            id,
            "Original Name",
            "Original Description",
            "original/source",
            "original/target",
            Set.of(new RuleCriteria(UUID.randomUUID(), "value", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS)),
            now,
            now
        );

        final var updatedRule = RuleUpdateVisitor.update(
            originalRule,
            "Updated Name",
            "Updated Description",
            Set.of(new RuleCriteria(UUID.randomUUID(), "new value", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS))
        );

        Assertions.assertThat(updatedRule)
            .isInstanceOf(MoveEmailRule.class)
            .satisfies(rule -> {
                final var moveRule = (MoveEmailRule) rule;
                Assertions.assertThat(moveRule.id()).isEqualTo(id);
                Assertions.assertThat(moveRule.name()).isEqualTo("Updated Name");
                Assertions.assertThat(moveRule.description()).isEqualTo("Updated Description");
                Assertions.assertThat(moveRule.sourceFolder()).isEqualTo("original/source");
                Assertions.assertThat(moveRule.targetFolder()).isEqualTo("original/target");
                Assertions.assertThat(moveRule.createdAt()).isEqualTo(now);
                Assertions.assertThat(moveRule.updatedAt()).isAfterOrEqualTo(now);
            });
    }

    @Test
    @DisplayName("Deve atualizar regra de arquivamento")
    void shouldUpdateArchiveRule() {
        final var id = UUID.randomUUID();
        final var now = LocalDateTime.now();
        final var originalRule = new ArchiveEmailRule(
            id,
            "Original Name",
            "Original Description",
            Set.of(new RuleCriteria(UUID.randomUUID(), "value", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS)),
            now,
            now
        );

        final var updatedRule = RuleUpdateVisitor.update(
            originalRule,
            "Updated Name",
            "Updated Description",
            Set.of(new RuleCriteria(UUID.randomUUID(), "new value", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS))
        );

        Assertions.assertThat(updatedRule)
            .isInstanceOf(ArchiveEmailRule.class)
            .satisfies(rule -> {
                Assertions.assertThat(rule.id()).isEqualTo(id);
                Assertions.assertThat(rule.name()).isEqualTo("Updated Name");
                Assertions.assertThat(rule.description()).isEqualTo("Updated Description");
                Assertions.assertThat(rule.createdAt()).isEqualTo(now);
                Assertions.assertThat(rule.updatedAt()).isAfterOrEqualTo(now);
            });
    }

    @Test
    @DisplayName("Deve atualizar regra de exclusão")
    void shouldUpdateDeleteRule() {
        final var id = UUID.randomUUID();
        final var now = LocalDateTime.now();
        final var originalRule = new DeleteEmailRule(
            id,
            "Original Name",
            "Original Description",
            Set.of(new RuleCriteria(UUID.randomUUID(), "value", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS)),
            now,
            now
        );

        final var updatedRule = RuleUpdateVisitor.update(
            originalRule,
            "Updated Name",
            "Updated Description",
            Set.of(new RuleCriteria(UUID.randomUUID(), "new value", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS))
        );

        Assertions.assertThat(updatedRule)
            .isInstanceOf(DeleteEmailRule.class)
            .satisfies(rule -> {
                Assertions.assertThat(rule.id()).isEqualTo(id);
                Assertions.assertThat(rule.name()).isEqualTo("Updated Name");
                Assertions.assertThat(rule.description()).isEqualTo("Updated Description");
                Assertions.assertThat(rule.createdAt()).isEqualTo(now);
                Assertions.assertThat(rule.updatedAt()).isAfterOrEqualTo(now);
            });
    }

}
