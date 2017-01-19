package com.googlecode.utterlyidle.monitoring;

import com.googlecode.totallylazy.json.Json;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public interface JsonLinesClient extends Closeable {
    void send(Object value) throws IOException;

    @Override
    default void close() throws IOException {
    }

    static JsonLinesClient jsonSender(SocketAddress socketAddress) throws IOException {
        TcpMessenger messenger = new TcpMessenger(SocketChannel.open(socketAddress));
        return new JsonLinesClient() {
            @Override
            public void send(final Object value) throws IOException {
                messenger.message(Json.json(value));
            }

            @Override
            public void close() throws IOException {
                messenger.close();
            }
        };
    }

    static JsonLinesClient noOp() {
        return value -> {
        };
    }
}
