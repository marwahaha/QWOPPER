package com.slowfrog.qwop;

public interface Log {
    void log(String message);

    void log(String message, Throwable e);

    void logf(String format, Object... args);
}
