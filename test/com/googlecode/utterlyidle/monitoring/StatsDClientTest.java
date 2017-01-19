package com.googlecode.utterlyidle.monitoring;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Assert.assertThat;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.utterlyidle.monitoring.StatsDClient.statsDClient;

public class StatsDClientTest {
    private List<String> messages = new ArrayList<>();
    private StatsDClient client = statsDClient(values -> messages.addAll(sequence(values)));

    @Test
    public void supportsGauge() throws Exception {
        client.gauge("foo.bar", 1);
        assertThat(messages, is(sequence("foo.bar:1|g")));
    }

    @Test
    public void supportsCounter() throws Exception {
        client.counter("foo.bar", 1);
        assertThat(messages, is(sequence("foo.bar:1|c")));
    }

    @Test
    public void supportsCounterWithSample() throws Exception {
        client.counter("foo.bar", 1, 0.5f);
        assertThat(messages, is(sequence("foo.bar:1|c|@0.500000")));
    }

    @Test
    public void supportsTimer() throws Exception {
        client.timer("foo.bar", 1);
        assertThat(messages, is(sequence("foo.bar:1|ms")));
    }

    @Test
    public void supportsHistogram() throws Exception {
        client.histogram("foo.bar", 1);
        assertThat(messages, is(sequence("foo.bar:1|h")));
    }

    @Test
    public void supportsMeter() throws Exception {
        client.meter("foo.bar", 1);
        assertThat(messages, is(sequence("foo.bar:1|m")));
    }
}