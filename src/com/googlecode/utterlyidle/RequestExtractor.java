package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;

public interface RequestExtractor<T> extends Extractor<Request, T>, Predicate<Request> {
}
