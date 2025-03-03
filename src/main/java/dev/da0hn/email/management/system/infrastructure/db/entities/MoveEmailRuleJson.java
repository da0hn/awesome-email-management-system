package dev.da0hn.email.management.system.infrastructure.db.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MoveEmailRuleJson extends RuleJson {

    @JsonProperty(value = "source_folder", required = true)
    private String sourceFolder;

    @JsonProperty(value = "target_folder", required = true)
    private String targetFolder;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("sourceFolder", this.sourceFolder)
            .append("targetFolder", this.targetFolder)
            .toString();
    }

}
