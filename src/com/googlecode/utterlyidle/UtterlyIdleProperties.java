package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.PrefixProperties;

import java.util.Properties;

public class UtterlyIdleProperties extends PrefixProperties {
    public static final String PREFIX = "utterlyidle";

    public UtterlyIdleProperties() {
        super(PREFIX, System.getProperties());
    }

    public UtterlyIdleProperties(Properties parent) {
        super(PREFIX, parent);
    }
}
