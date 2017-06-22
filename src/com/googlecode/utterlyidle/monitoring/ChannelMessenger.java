package com.googlecode.utterlyidle.monitoring;

import com.googlecode.totallylazy.LazyException;

import java.io.File;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Sequences.sequence;
import static java.nio.ByteBuffer.wrap;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

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

    public static ChannelMessenger<DatagramChannel> udpMessager(SocketAddress socketAddress) {
        try {
            return new ChannelMessenger<>(DatagramChannel.open().connect(socketAddress));
        } catch (IOException e) {
            throw LazyException.lazyException(e);
        }
    }

    public static ChannelMessenger<SocketChannel> tcpMessager(SocketAddress socketAddress) {
        try {
            return new ChannelMessenger<>(SocketChannel.open(socketAddress));
        } catch (IOException e) {
            throw LazyException.lazyException(e);
        }
    }

    public static ChannelMessenger<FileChannel> fileMessenger(File file)  {
        try {
            return new ChannelMessenger<>(FileChannel.open(file.toPath(), WRITE, APPEND, CREATE));
        } catch (IOException e) {
            throw LazyException.lazyException(e);
        }
    }
}
