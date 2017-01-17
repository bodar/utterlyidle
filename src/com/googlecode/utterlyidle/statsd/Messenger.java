package com.googlecode.utterlyidle.statsd;

import java.io.Closeable;
import java.io.IOException;

import static com.googlecode.totallylazy.Sequences.cons;
import static com.googlecode.totallylazy.Sequences.sequence;

public interface Messenger extends Closeable {
    void message(Iterable<? extends String> values) throws IOException;

    default void message(String head, String... tail) throws IOException {
        message(cons(head, sequence(tail)));
    }

    @Override
    default void close() throws IOException {
    }

    static Messenger noOp() {
        return value -> { };
    }
}
