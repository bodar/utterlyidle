package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Value;

import java.util.UUID;

public class ApplicationId implements Value<UUID> {
    private final UUID value;

    private ApplicationId() {
        this.value = UUID.randomUUID();
    }

    public static ApplicationId applicationId() {
        return new ApplicationId();
    }

    @Override
    public UUID value() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ApplicationId && ((ApplicationId) obj).value().equals(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
