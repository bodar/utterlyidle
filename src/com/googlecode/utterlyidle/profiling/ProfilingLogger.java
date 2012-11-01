package com.googlecode.utterlyidle.profiling;

import com.googlecode.lazyrecords.Logger;

import java.util.Map;

public class ProfilingLogger implements Logger {
    private final Logger logger;
    private final ProfilingData data;

    public ProfilingLogger(Logger logger, ProfilingData data) {
        this.logger = logger;
        this.data = data;
    }

    @Override
    public Logger log(Map<String, ?> stringMap) {
        data.log(stringMap);
        logger.log(stringMap);
        return this;
    }
}
