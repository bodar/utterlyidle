package com.googlecode.utterlyidle.services;

import com.googlecode.totallylazy.Block;

public interface Service {
    void start() throws Exception;

    void stop() throws Exception;

    class functions {
        public static Block<Service> start() {
            return new Block<Service>() {
                @Override
                public void execute(Service service) throws Exception {
                    service.start();
                }
            };
        }
    }

}
