package com.googlecode.utterlyidle.statsd;

import com.googlecode.totallylazy.LazyException;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

public class TcpMessenger extends ChannelMessenger<SocketChannel> {
    public TcpMessenger(final SocketChannel channel) {
        super(channel);
    }

    public static TcpMessenger udpMessager(SocketAddress socketAddress) {
        try {
            return new TcpMessenger(SocketChannel.open(socketAddress));
        } catch (IOException e) {
            throw LazyException.lazyException(e);
        }
    }
}
