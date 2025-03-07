package dev.da0hn.email.management.system.core.domain;

import java.util.Set;

public record RuleUpdateData(
    String name,
    String description,
    Set<RuleCriteria> criteria,
    String sourceFolder,
    String targetFolder
) {}
