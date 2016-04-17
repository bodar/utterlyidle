package com.googlecode.utterlyidle.statsd;

import com.googlecode.totallylazy.LazyException;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Sequences.sequence;
import static java.nio.ByteBuffer.wrap;

public class UdpMessenger implements Messenger {
    private final DatagramChannel channel;

    public UdpMessenger(DatagramChannel channel) {
        this.channel = channel;
    }


    public static UdpMessenger udpMessager(SocketAddress socketAddress) {
        try {
            return new UdpMessenger(DatagramChannel.open().connect(socketAddress));
        } catch (IOException e) {
            throw LazyException.lazyException(e);
        }
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
