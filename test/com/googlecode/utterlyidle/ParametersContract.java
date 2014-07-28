package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.totallylazy.matchers.IterableMatcher.hasExactly;
import static org.junit.Assert.assertThat;

public abstract class ParametersContract<T extends Parameters<String, String, T>> {
    protected abstract T parameters();

    @Test
    public void canReplaceParameters() throws Exception {
        T result = parameters().add("message", "of peace").add("message", "of war").replace("message", "hello world");

        assertThat(result.getValues("message"), hasExactly("hello world"));
    }
}
