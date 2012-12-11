package com.googlecode.utterlyidle.rendering.exceptions;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.Request;

import java.util.Date;

public class StoredException {
    private final Date date;
    private final Request request;
    private final String exception;

    public StoredException(Date date, Request request, String exception) {
        this.date = date;
        this.request = request;
        this.exception = exception;
    }

    public Date getDate() {
        return date;
    }

    public Request getRequest() {
        return request;
    }

    public String getException() {
        return exception;
    }

    public static Callable1<? super StoredException, String> exception() {
        return new Callable1<StoredException, String>() {
            @Override
            public String call(StoredException e) throws Exception {
                return e.getException();
            }
        };
    }
}
