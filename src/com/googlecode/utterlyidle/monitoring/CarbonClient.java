package com.googlecode.utterlyidle.monitoring;

import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Seconds;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import static java.lang.String.format;

public interface CarbonClient extends Closeable {
    /**
     * A gauge is an instantaneous measurement of a value, like the gas gauge in a car. It differs from a counter by being
     * calculated at the client rather than the server.
     **/
    void gauge(String name, long value) throws IOException;

    @Override
    default void close() throws IOException {
    }

    static CarbonClient carbonClient(SocketAddress socketAddress, Clock clock) throws IOException {
        ChannelMessenger messenger = ChannelMessenger.tcpMessager(socketAddress);
        return new CarbonClient() {
            @Override
            public void gauge(final String name, final long value) throws IOException {
                messenger.message(format("%s %s %s", name, value, Seconds.sinceEpoch(clock.now())));
            }

            @Override
            public void close() throws IOException {
                messenger.close();
            }
        };
    }

    static CarbonClient noOp() {
        return (name, value) -> {
        };
    }
}
