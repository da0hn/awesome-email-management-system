package dev.da0hn.email.management.system.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RuleUpdateVisitorTest {

    @Test
    @DisplayName("Should update move rule with new folders")
    void shouldUpdateMoveRuleWithNewFolders() {
        // given
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

        // when
        final var updatedRule = RuleUpdateVisitor.updateMove(
            originalRule,
            "Updated Name",
            "Updated Description",
            Set.of(new RuleCriteria(UUID.randomUUID(), "new value", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS)),
            "new/source",
            "new/target"
        );

        // then
        assertThat(updatedRule)
            .isInstanceOf(MoveEmailRule.class)
            .satisfies(rule -> {
                final var moveRule = (MoveEmailRule) rule;
                assertThat(moveRule.id()).isEqualTo(id);
                assertThat(moveRule.name()).isEqualTo("Updated Name");
                assertThat(moveRule.description()).isEqualTo("Updated Description");
                assertThat(moveRule.sourceFolder()).isEqualTo("new/source");
                assertThat(moveRule.targetFolder()).isEqualTo("new/target");
                assertThat(moveRule.createdAt()).isEqualTo(now);
                assertThat(moveRule.updatedAt()).isAfter(now);
            });
    }

    @Test
    @DisplayName("Should update move rule keeping original folders")
    void shouldUpdateMoveRuleKeepingOriginalFolders() {
        // given
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

        // when
        final var updatedRule = RuleUpdateVisitor.update(
            originalRule,
            "Updated Name",
            "Updated Description",
            Set.of(new RuleCriteria(UUID.randomUUID(), "new value", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS))
        );

        // then
        assertThat(updatedRule)
            .isInstanceOf(MoveEmailRule.class)
            .satisfies(rule -> {
                final var moveRule = (MoveEmailRule) rule;
                assertThat(moveRule.id()).isEqualTo(id);
                assertThat(moveRule.name()).isEqualTo("Updated Name");
                assertThat(moveRule.description()).isEqualTo("Updated Description");
                assertThat(moveRule.sourceFolder()).isEqualTo("original/source");
                assertThat(moveRule.targetFolder()).isEqualTo("original/target");
                assertThat(moveRule.createdAt()).isEqualTo(now);
                assertThat(moveRule.updatedAt()).isAfter(now);
            });
    }

    @Test
    @DisplayName("Should update archive rule")
    void shouldUpdateArchiveRule() {
        // given
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

        // when
        final var updatedRule = RuleUpdateVisitor.update(
            originalRule,
            "Updated Name",
            "Updated Description",
            Set.of(new RuleCriteria(UUID.randomUUID(), "new value", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS))
        );

        // then
        assertThat(updatedRule)
            .isInstanceOf(ArchiveEmailRule.class)
            .satisfies(rule -> {
                assertThat(rule.id()).isEqualTo(id);
                assertThat(rule.name()).isEqualTo("Updated Name");
                assertThat(rule.description()).isEqualTo("Updated Description");
                assertThat(rule.createdAt()).isEqualTo(now);
                assertThat(rule.updatedAt()).isAfter(now);
            });
    }

    @Test
    @DisplayName("Should update delete rule")
    void shouldUpdateDeleteRule() {
        // given
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

        // when
        final var updatedRule = RuleUpdateVisitor.update(
            originalRule,
            "Updated Name",
            "Updated Description",
            Set.of(new RuleCriteria(UUID.randomUUID(), "new value", RuleCriteriaType.SUBJECT, RuleCriteriaOperator.CONTAINS))
        );

        // then
        assertThat(updatedRule)
            .isInstanceOf(DeleteEmailRule.class)
            .satisfies(rule -> {
                assertThat(rule.id()).isEqualTo(id);
                assertThat(rule.name()).isEqualTo("Updated Name");
                assertThat(rule.description()).isEqualTo("Updated Description");
                assertThat(rule.createdAt()).isEqualTo(now);
                assertThat(rule.updatedAt()).isAfter(now);
            });
    }
}
