package com.googlecode.utterlyidle;

import java.io.Closeable;
import java.io.OutputStream;

public interface Response extends Closeable {
    Status status();

    Response status(Status value);

    String header(String name);

    Iterable<String> headers(String name);

    Response header(String name, String value);

    OutputStream output();

    Object entity();

    Response entity(Object value);
}
