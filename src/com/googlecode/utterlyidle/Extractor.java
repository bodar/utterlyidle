package com.googlecode.utterlyidle;

public interface Extractor<T, S> {
    S extract(T t);
}
