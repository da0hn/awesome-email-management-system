package dev.da0hn.email.management.system.core.ports.api.dto;

import jakarta.validation.constraints.NotBlank;

public record MoveRuleInput(
    @NotBlank(message = "Source folder is required")
    String sourceFolder,
    @NotBlank(message = "Target folder is required")
    String targetFolder
) {}
