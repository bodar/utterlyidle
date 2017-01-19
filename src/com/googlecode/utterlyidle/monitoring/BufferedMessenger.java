package com.googlecode.utterlyidle.monitoring;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.googlecode.totallylazy.Sequences.sequence;

public class BufferedMessenger implements Messenger {
    private final Messenger messenger;
    private final List<String> buffer = new CopyOnWriteArrayList<>();

    public BufferedMessenger(Messenger messenger) {
        this.messenger = messenger;
    }

    @Override
    public void message(Iterable<? extends String> values) throws IOException {
        buffer.addAll(sequence(values));
    }

    @Override
    public void close() throws IOException {
        messenger.message(buffer);
        buffer.clear();
    }
}
