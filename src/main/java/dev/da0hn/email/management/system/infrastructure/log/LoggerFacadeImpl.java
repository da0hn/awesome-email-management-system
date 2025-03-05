package dev.da0hn.email.management.system.infrastructure.log;

import dev.da0hn.email.management.system.core.domain.SensitiveData;
import dev.da0hn.email.management.system.core.ports.spi.LoggerFacade;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerFacadeImpl implements LoggerFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerFacadeImpl.class);

    private final HashMap<String, Object> parameters = new HashMap<>();

    private String who;

    private String what;

    private Class<?> where;

    private String method;

    private LogLevel level;

    public LoggerFacadeImpl() { }

    @Override
    public LoggerFacade who(final String who) {
        this.who = who;
        return this;
    }

    @Override
    public LoggerFacade what(final String what) {
        this.what = what;
        return this;
    }

    @Override
    public <T> LoggerFacade where(final T where) {
        this.where = where.getClass();
        return this;
    }

    @Override
    public LoggerFacade method(final String method) {
        this.method = method;
        return this;
    }

    @Override
    public LoggerFacade level(final LogLevel level) {
        this.level = level;
        return this;
    }

    @Override
    public LoggerFacade parameter(final String param, final Object value) {
        this.parameters.put(param, maskSensitiveData(value));
        return this;
    }

    private Object maskSensitiveData(final Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof char[]) {
            return "[PROTECTED]";
        }
        if (value instanceof SensitiveData) {
            return value.toString(); // SensitiveData implementations should mask data in toString()
        }
        return value;
    }

    /**
     * Log the message.
     *
     * <p>The message is logged with the following format:</p>
     * <p>[WHERE: where] [METHOD: method] [WHO: who] [WHAT: what]</p>
     * <p>The log level is determined by the {@link LogLevel} enum.</p>
     * <p>If the log level is not set, it defaults to {@link LogLevel#INFO}.</p>
     * <p>If the who is not set, it defaults to "UNKNOWN".</p>
     * <p>If the where is not set, it is not logged.</p>
     * <p>If the method is not set, it is not logged.</p>
     * <p>If the what is not set, it is not logged.</p>
     */
    @Override
    public void log() {
        final StringBuilder logMessage = new StringBuilder();

        final var level = Optional.ofNullable(this.level).orElse(LogLevel.INFO);
        final var who = Optional.ofNullable(this.who).orElse("UNKNOWN");

        if (this.where != null) logMessage.append("[WHERE: ").append(this.where).append("] ");
        if (this.method != null) logMessage.append("[METHOD: ").append(this.method).append("] ");

        if (!this.parameters.isEmpty()) {
            final var result = this.parameters.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + maskSensitiveData(entry.getValue()))
                .collect(Collectors.joining(", ", "(", ")"));
            logMessage.append("[PARAMS: ").append(result).append("] ");
        }

        logMessage.append("[WHO: ").append(who).append("] ");
        if (this.what != null) logMessage.append("[WHAT: ").append(this.what).append("] ");

        switch (level) {
            case INFO -> LOGGER.info(logMessage.toString());
            case DEBUG -> LOGGER.debug(logMessage.toString());
            case ERROR -> LOGGER.error(logMessage.toString());
            case WARN -> LOGGER.warn(logMessage.toString());
            default -> LOGGER.trace(logMessage.toString());
        }
    }

}
