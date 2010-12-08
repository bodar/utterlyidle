package com.googlecode.utterlyidle;

public interface Redirect {
    String location();

    void applyTo(BasePath base, Response response);
}
