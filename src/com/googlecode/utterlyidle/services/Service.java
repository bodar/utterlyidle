package com.googlecode.utterlyidle.services;

import com.googlecode.totallylazy.Block;

public interface Service {
    void start() throws Exception;

    void stop() throws Exception;

    class functions {
        public static Block<Service> start() {
            return Service::start;
        }
    }
}