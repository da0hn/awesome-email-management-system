package dev.da0hn.email.management.system.core.domain;

import java.io.Serial;
import java.io.Serializable;

import lombok.Builder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Builder
public class AccountCredentials implements Serializable {

    @Serial
    private static final long serialVersionUID = -2235376574903307288L;

    private final String email;

    private final String password;

    public AccountCredentials(final String email, final String password) {
        this.email = email;
        this.password = password;
    }

    public String email() {
        return this.email;
    }

    public String password() {
        return this.password;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("email", this.email)
            .append("password", this.password)
            .toString();
    }

}
