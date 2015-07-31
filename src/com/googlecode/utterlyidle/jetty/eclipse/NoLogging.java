package com.googlecode.utterlyidle.jetty.eclipse;

import org.eclipse.jetty.util.log.Logger;

public class NoLogging implements Logger {
    @Override
    public String getName() {
        return "NoLogging";
    }

    @Override
    public void warn(final String msg, final Object... args) {

    }

    @Override
    public void warn(final Throwable thrown) {

    }

    @Override
    public void warn(final String msg, final Throwable thrown) {

    }

    @Override
    public void info(final String msg, final Object... args) {

    }

    @Override
    public void info(final Throwable thrown) {

    }

    @Override
    public void info(final String msg, final Throwable thrown) {

    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void setDebugEnabled(final boolean enabled) {

    }

    @Override
    public void debug(final String msg, final Object... args) {

    }

    @Override
    public void debug(final String msg, final long value) {

    }

    @Override
    public void debug(final Throwable thrown) {

    }

    @Override
    public void debug(final String msg, final Throwable thrown) {

    }

    @Override
    public Logger getLogger(final String name) {
        return this;
    }

    @Override
    public void ignore(final Throwable ignored) {

    }
}
