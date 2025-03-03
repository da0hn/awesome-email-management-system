package dev.da0hn.email.management.system.infrastructure.db.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaOperator;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@Builder
@AllArgsConstructor
public class RuleCriteriaJson implements Serializable {

    @Serial
    private static final long serialVersionUID = 8848035889332152582L;

    @JsonProperty(value = "criteria_id", required = true)
    private UUID criteriaId;

    private String value;

    private RuleCriteriaOperator operator;

    private RuleCriteriaType type;

    @Override
    public int hashCode() {
        return Objects.hashCode(this.criteriaId);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) return false;

        final RuleCriteriaJson that = (RuleCriteriaJson) o;
        return Objects.equals(this.criteriaId, that.criteriaId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("criteriaId", this.criteriaId)
            .append("value", this.value)
            .append("operator", this.operator)
            .append("type", this.type)
            .toString();
    }

}
