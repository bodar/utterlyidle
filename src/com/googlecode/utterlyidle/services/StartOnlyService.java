package com.googlecode.utterlyidle.services;

public abstract class StartOnlyService implements Service {
    @Override
    public void stop() throws Exception {}
}
