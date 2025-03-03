package dev.da0hn.email.management.system.infrastructure.db.entities;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.da0hn.email.management.system.core.domain.RuleAction;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@SuperBuilder
public class MoveEmailRuleJson extends RuleJson {

    @JsonProperty(value = "source_folder", required = true)
    private final String sourceFolder;

    @JsonProperty(value = "target_folder", required = true)
    private final String targetFolder;

    public MoveEmailRuleJson(
        final UUID ruleId,
        final String name,
        final String description,
        final String sourceFolder,
        final String targetFolder,
        final List<RuleCriteriaJson> criteria
    ) {
        super(ruleId, name, description, RuleAction.MOVE, criteria);
        this.sourceFolder = sourceFolder;
        this.targetFolder = targetFolder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("sourceFolder", this.sourceFolder)
            .append("targetFolder", this.targetFolder)
            .toString();
    }

}
