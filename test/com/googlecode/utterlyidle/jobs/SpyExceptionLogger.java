package com.googlecode.utterlyidle.jobs;

import com.googlecode.utterlyidle.ExceptionLogger;

public class SpyExceptionLogger implements ExceptionLogger {
    public boolean hasLogged;

    @Override
    public void log(final Exception ex) {
        hasLogged = true;
    }
}
