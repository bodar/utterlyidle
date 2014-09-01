package com.googlecode.utterlyidle;

import static com.googlecode.totallylazy.Debug.trace;

public class DebuggingOnlyExceptionLogger implements ExceptionLogger {
    @Override
    public void log(final Exception e) {
        trace(e);
    }
}
