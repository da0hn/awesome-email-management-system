package dev.da0hn.email.management.system.core.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class RuleCriteria implements Serializable {

    @Serial
    private static final long serialVersionUID = -8042461158945083661L;

    private final UUID id;

    private final String value;

    private final RuleCriteriaType type;

    private final RuleCriteriaOperator operator;

    public RuleCriteria(final UUID id, final String value, final RuleCriteriaType type, final RuleCriteriaOperator operator) {
        this.id = id;
        this.value = value;
        this.type = type;
        this.operator = operator;
    }

    public String value() {
        return this.value;
    }

    public RuleCriteriaType type() {
        return this.type;
    }

    public RuleCriteriaOperator operator() {
        return this.operator;
    }

    public UUID id() {
        return this.id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("value", this.value)
            .append("type", this.type)
            .append("operator", this.operator)
            .toString();
    }

}
