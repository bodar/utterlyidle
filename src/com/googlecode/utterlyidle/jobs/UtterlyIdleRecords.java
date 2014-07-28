package com.googlecode.utterlyidle.jobs;

import com.googlecode.lazyrecords.Records;
import com.googlecode.totallylazy.Value;

public class UtterlyIdleRecords implements Value<Records> {
    private final Records records;

    public UtterlyIdleRecords(final Records records) {
        this.records = records;
    }

    @Override
    public Records value() {
        return records;
    }
}
