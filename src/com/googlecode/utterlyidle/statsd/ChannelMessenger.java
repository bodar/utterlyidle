package com.googlecode.utterlyidle.statsd;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Sequences.sequence;
import static java.nio.ByteBuffer.wrap;

public class ChannelMessenger<C extends WritableByteChannel> implements Messenger {
    protected final C channel;

    public ChannelMessenger(C channel) {
        this.channel = channel;
    }

    @Override
    public void message(Iterable<? extends String> values) throws IOException {
        channel.write(wrap(bytes(sequence(values).toString("\n"))));
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
