package com.googlecode.utterlyidle.monitoring;

import com.googlecode.totallylazy.LazyException;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Sequences.sequence;
import static java.nio.ByteBuffer.wrap;

public class UdpMessenger extends ChannelMessenger<DatagramChannel> {
    public UdpMessenger(final DatagramChannel channel) {
        super(channel);
    }

    public static UdpMessenger udpMessager(SocketAddress socketAddress) {
        try {
            return new UdpMessenger(DatagramChannel.open().connect(socketAddress));
        } catch (IOException e) {
            throw LazyException.lazyException(e);
        }
    }
}
