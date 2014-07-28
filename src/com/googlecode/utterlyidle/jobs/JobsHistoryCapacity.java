package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Value;
import com.googlecode.utterlyidle.UtterlyIdleProperties;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

public class JobsHistoryCapacity implements Value<Integer> {
    public static final String PROPERTY_NAME = "jobs.history.capacity";
    public static final int DEFAULT = 20;
    private final int value;

    public JobsHistoryCapacity(int capacity) {
        this.value = capacity;
    }

    public JobsHistoryCapacity(UtterlyIdleProperties properties) {
        this(parseInt(properties.getProperty(PROPERTY_NAME, valueOf(DEFAULT))));
    }

    @Override
    public Integer value() {
        return value;
    }
}
