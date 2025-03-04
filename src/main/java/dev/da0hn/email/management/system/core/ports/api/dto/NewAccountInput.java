package dev.da0hn.email.management.system.core.ports.api.dto;

public record NewAccountInput(
    String name,
    Credentials credentials,
    ConnectionDetails connectionDetails
) {

    public record Credentials(String email, String password) {
    }

    public record ConnectionDetails(String host, Integer port, String protocol) {

    }

}
