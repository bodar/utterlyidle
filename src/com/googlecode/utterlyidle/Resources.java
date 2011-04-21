package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.dsl.ActivatorBuilder;

public interface Resources extends Activators {
    void add(Class resource);

    void add(Activator activator);

    void add(ActivatorBuilder activator);
}
