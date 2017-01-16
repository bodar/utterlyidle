package com.googlecode.utterlyidle.graphite;

import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Seconds;
import com.googlecode.utterlyidle.statsd.TcpMessenger;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import static java.lang.String.format;

public interface CarbonSender extends Closeable {
    void counter(String name, long value) throws IOException;

    @Override
    default void close() throws IOException {
    }

    static CarbonSender carbonSender(SocketAddress socketAddress, Clock clock) throws IOException {
        TcpMessenger messenger = new TcpMessenger(SocketChannel.open(socketAddress));
        return new CarbonSender() {
            @Override
            public void counter(final String name, final long value) throws IOException {
                messenger.message(format("%s %s %s", name, value, Seconds.sinceEpoch(clock.now())));
            }

            @Override
            public void close() throws IOException {
                messenger.close();
            }
        };
    }

    static CarbonSender noOp() {
        return (name, value) -> {
        };
    }
}
