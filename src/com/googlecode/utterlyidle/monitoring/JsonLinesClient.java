package com.googlecode.utterlyidle.monitoring;

import com.googlecode.totallylazy.json.Json;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

import static com.googlecode.utterlyidle.monitoring.ChannelMessenger.fileMessenger;
import static com.googlecode.utterlyidle.monitoring.ChannelMessenger.tcpMessager;

public interface JsonLinesClient extends Closeable {
    void send(Object value) throws IOException;

    @Override
    default void close() throws IOException {
    }

    static JsonLinesClient jsonClient(SocketAddress socketAddress) throws IOException {
        return jsonLinesClient(socketAddress);
    }

    static JsonLinesClient jsonLinesClient(File file) throws IOException {
        return jsonLinesClient(fileMessenger(file));
    }

    static JsonLinesClient jsonLinesClient(SocketAddress socketAddress) throws IOException {
        return jsonLinesClient(tcpMessager(socketAddress));
    }

    static JsonLinesClient jsonLinesClient(ChannelMessenger<?> messenger) {
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
