package dev.da0hn.email.management.system.infrastructure.db.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.da0hn.email.management.system.core.domain.RuleAction;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ArchiveEmailRuleJson.class, name = "ARCHIVE"),
    @JsonSubTypes.Type(value = DeleteEmailRuleJson.class, name = "DELETE"),
    @JsonSubTypes.Type(value = MoveEmailRuleJson.class, name = "MOVE")
})
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RuleJson {

    @NotNull(message = "Rule ID is required")
    @JsonProperty(value = "rule_id", required = true)
    private UUID ruleId;

    private String name;

    private String description;

    private RuleAction action;

    private List<RuleCriteriaJson> criteria;

    @JsonProperty(value = "created_at")
    private LocalDateTime createdAt;

    @JsonProperty(value = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public int hashCode() {
        return Objects.hashCode(this.ruleId);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) return false;

        final RuleJson ruleJson = (RuleJson) o;
        return Objects.equals(this.ruleId, ruleJson.ruleId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("ruleId", this.ruleId)
            .append("name", this.name)
            .append("description", this.description)
            .append("action", this.action)
            .append("criteria", this.criteria)
            .append("createdAt", this.createdAt)
            .append("updatedAt", this.updatedAt)
            .toString();
    }

}
