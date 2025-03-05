package dev.da0hn.email.management.system.core.ports.api.dto;

import dev.da0hn.email.management.system.core.domain.SensitiveData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewAccountInput(
    @NotBlank(message = "Name is required")
    String name,

    @NotNull(message = "Credentials are required")
    @Valid
    Credentials credentials,

    @NotNull(message = "Connection details are required")
    @Valid
    ConnectionDetails connectionDetails
) {

    public record Credentials(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password
    ) implements SensitiveData {
        public Credentials {
            if (password != null && password.isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }
        }

        @Override
        public String toString() {
            return "Credentials[email=" + email + ", password=[PROTECTED]]";
        }
    }

    public record ConnectionDetails(
        @NotBlank(message = "Host is required")
        String host,

        @NotNull(message = "Port is required")
        @Min(value = 1, message = "Port must be between 1 and 65535")
        @Max(value = 65535, message = "Port must be between 1 and 65535")
        Integer port,

        @NotBlank(message = "Protocol is required")
        @Pattern(
            regexp = "^(smtp|imap|pop3)$",
            message = "Protocol must be one of: smtp, imap, pop3"
        )
        String protocol
    ) {
    }
}
