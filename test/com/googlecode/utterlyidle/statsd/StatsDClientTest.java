package com.googlecode.utterlyidle.statsd;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Assert.assertThat;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.utterlyidle.statsd.StatsDClient.statsDClient;

@Ignore("Manual test currently")
public class StatsDClientTest {
    List<String> messages = new ArrayList<>();
    StatsDClient client = statsDClient(messages::add);

    @Test
    public void supportsGauge() throws Exception {
        client.gauge("foo.bar", 1);
        assertThat(messages, is(sequence("foo.bar:1|g")));
    }

    @Test
    public void supportsMeter() throws Exception {
        client.meter("foo.bar", 1);
        assertThat(messages, is(sequence("foo.bar:1|m")));
    }
}