package dev.da0hn.email.management.system.core.ports.api.dto;

import dev.da0hn.email.management.system.core.domain.RuleAction;
import dev.da0hn.email.management.system.core.ports.api.validation.RuleValidatable;
import dev.da0hn.email.management.system.core.ports.api.validation.ValidRule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

@ValidRule
public record UpdateRuleInput(
    @NotNull(message = "ID da regra é obrigatório")
    UUID ruleId,
    @NotNull(message = "ID da conta é obrigatório")
    UUID accountId,
    @NotBlank(message = "Nome da regra é obrigatório")
    @Size(min = 3, max = 50, message = "Nome da regra deve ter entre 3 e 50 caracteres")
    String name,
    @NotBlank(message = "Descrição da regra é obrigatória")
    @Size(max = 255, message = "Descrição da regra não deve exceder 255 caracteres")
    String description,
    @NotNull(message = "Ação da regra é obrigatória")
    RuleAction action,
    @NotEmpty(message = "Pelo menos um critério é obrigatório")
    @Size(max = 10, message = "Máximo de 10 critérios permitidos")
    @Valid
    Set<NewRuleCriteriaInput> criteria,
    @Valid
    MoveRuleInput moveRule
) implements RuleValidatable {}
