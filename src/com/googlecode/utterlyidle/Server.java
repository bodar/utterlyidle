package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.Url;

import java.io.Closeable;

public interface Server extends Closeable {
    Url getUrl();
}
