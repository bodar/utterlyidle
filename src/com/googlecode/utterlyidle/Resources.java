package com.googlecode.utterlyidle;

public interface Resources extends Activators {
    void add(Class resource);

    void add(Activator activator);
}
