package com.googlecode.utterlyidle;

public interface Server {
    Server start() throws Exception;

    Server stop() throws Exception;
}
