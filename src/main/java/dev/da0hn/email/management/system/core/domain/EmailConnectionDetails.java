package dev.da0hn.email.management.system.core.domain;

import java.io.Serial;
import java.io.Serializable;

import lombok.Builder;

@Builder
public class EmailConnectionDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 7775639076291250488L;

    private final String host;

    private final int port;

    private final String protocol;

    public EmailConnectionDetails(final String host, final int port, final String protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
    }

    public String host() {
        return this.host;
    }

    public int port() {
        return this.port;
    }

    public String protocol() {
        return this.protocol;
    }

}
