package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Function1;

import java.util.UUID;
import java.util.concurrent.Callable;

public class UUIDActivator implements Callable<UUID> {
    private final String value;

    private UUIDActivator(String value) {
        this.value = value;
    }

    public static UUIDActivator uuidActivator(String value) {
        return new UUIDActivator(value);
    }

    public UUID call() throws Exception {
        return fromString().call(value);
    }

    public static Function1<String, UUID> fromString() {
        return new Function1<String, UUID>() {
            @Override
            public UUID call(String value) throws Exception {
                return UUID.fromString(value);
            }
        };
    }


}
