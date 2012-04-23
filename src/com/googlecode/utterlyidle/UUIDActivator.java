package com.googlecode.utterlyidle;

import java.util.UUID;
import java.util.concurrent.Callable;

public class UUIDActivator implements Callable<UUID> {
    private final String value;

    public UUIDActivator(String value) {
        this.value = value;
    }

    public UUID call() throws Exception {
        return UUID.fromString(value);
    }
}
