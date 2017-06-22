package com.googlecode.utterlyidle.monitoring;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static com.googlecode.utterlyidle.monitoring.ChannelMessenger.udpMessager;
import static java.lang.String.format;

/**
 * Taken from https://github.com/b/statsd_spec
 */
public interface StatsDClient extends Closeable {
    static StatsDClient statsDClient(String host, int port) throws IOException {
        return statsDClient(new InetSocketAddress(host, port));
    }

    static StatsDClient statsDClient(SocketAddress socketAddress) throws IOException {
        return statsDClient(udpMessager(socketAddress));
    }

    static StatsDClient statsDClient(final Messenger messenger) {
        return () -> messenger;
    }

    static StatsDClient noOp() {
        return Messenger::noOp;
    }

    @Override
    default void close() throws IOException{
        messager().close();
    }

    Messenger messager();

    /**
     * A gauge is an instantaneous measurement of a value, like the gas gauge in a car. It differs from a counter by being
     * calculated at the client rather than the server. Valid gauge values are in the range [0, 2^64^)
     * <p>
     * metric name:value|g
     */
    default void gauge(String name, long value) throws IOException {
        assert value >= 0;
        messager().message(format("%s:%d|g", name, value));
    }

    /**
     * A counter is a gauge calculated at the server. Metrics sent by the client increment or decrement the value of the
     * gauge rather than giving its current value. Counters may also have an associated sample rate, given as a decimal of
     * the number of samples per event count. For example, a sample rate of 1/10 would be exported as 0.1. Valid counter
     * values are in the range (-2^63^, 2^63^).
     * <p>
     * metric name:value|c
     */
    default void counter(String name, long value) throws IOException {
        messager().message(format("%s:%d|c", name, value));
    }

    /**
     * A counter is a gauge calculated at the server. Metrics sent by the client increment or decrement the value of the
     * gauge rather than giving its current value. Counters may also have an associated sample rate, given as a decimal of
     * the number of samples per event count. For example, a sample rate of 1/10 would be exported as 0.1. Valid counter
     * values are in the range (-2^63^, 2^63^).
     * <p>
     * metric name:value|c|@sample rate
     */
    default void counter(String name, long value, float sampleRate) throws IOException {
        messager().message(format("%s:%d|c|@%f", name, value, sampleRate));
    }

    /**
     * A timer is a measure of the number of milliseconds elapsed between a start and end time, for example the time to
     * complete rendering of a web page for a user. Valid timer values are in the range [0, 2^64^).
     * <p>
     * metric name:value|ms
     */
    default void timer(String name, long value) throws IOException {
        assert value >= 0;
        messager().message(format("%s:%d|ms", name, value));
    }

    /**
     * A histogram is a measure of the distribution of timer values over time, calculated at the server. As the data
     * exported for timers and histograms is the same, this is currently an alias for a timer. Valid histogram values are
     * in the range [0, 2^64^).
     * <p>
     * metric name:value|h
     */
    default void histogram(String name, long value) throws IOException {
        assert value >= 0;
        messager().message(format("%s:%d|h", name, value));
    }

    /**
     * A meter measures the rate of events over time, calculated at the server. They may also be thought of as
     * increment-only counters. Valid meter values are in the range [0, 2^64^).
     * <p>
     * metric name:value|m
     */
    default void meter(String name, long value) throws IOException {
        assert value >= 0;
        messager().message(format("%s:%d|m", name, value));
    }

}
