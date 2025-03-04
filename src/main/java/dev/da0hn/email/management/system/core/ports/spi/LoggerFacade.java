package dev.da0hn.email.management.system.core.ports.spi;

import dev.da0hn.email.management.system.infrastructure.log.LoggerFacadeImpl;

public interface LoggerFacade {

    static LoggerFacade instance() {
        return new LoggerFacadeImpl();
    }

    LoggerFacade who(String who);

    LoggerFacade what(String what);

    <T> LoggerFacade where(T where);

    LoggerFacade method(String method);

    LoggerFacade level(LogLevel level);

    LoggerFacade parameter(String param, Object value);

    void log();

    enum LogLevel {
        INFO,
        DEBUG,
        ERROR,
        WARN,
        TRACE
    }

}
