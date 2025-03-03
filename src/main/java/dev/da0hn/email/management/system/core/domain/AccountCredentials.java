package dev.da0hn.email.management.system.core.domain;

import java.io.Serial;
import java.io.Serializable;

import lombok.Builder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Builder
public class AccountCredentials implements Serializable {

    @Serial
    private static final long serialVersionUID = -2235376574903307288L;

    private final String username;

    private final String password;

    public AccountCredentials(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public String username() {
        return this.username;
    }

    public String password() {
        return this.password;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("username", this.username)
            .append("password", this.password)
            .toString();
    }

}
