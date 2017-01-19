package com.googlecode.utterlyidle.monitoring;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Seconds;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Date;
import java.util.Map;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.json.Json.json;
import static com.googlecode.totallylazy.security.GZip.gzip;
import static java.nio.ByteBuffer.wrap;

public interface GelfClient {
    void send(String host, String shortMessage, Option<Date> timestamp, Severity severity, Map<String, Object> data);

    static GelfClient udpGelfClient(SocketAddress socketAddress, Clock clock) throws IOException {
        DatagramChannel channel = DatagramChannel.open().connect(socketAddress);
        return (host, shortMessage, timestamp, severity, data) -> {
            Date time = timestamp.getOrElse(clock.now());
            try {
                channel.write(wrap(gzip(bytes(json(gelf(host, shortMessage, severity, time, data))))));
            } catch (IOException e) {
                throw LazyException.lazyException(e);
            }
        };
    }

    static Map<String, Object> gelf(String host, String shortMessage, Severity severity, Date time, Map<String, Object> data) {
        Map<String, Object> gelf = Maps.map(
                "version", "1.1",
                "host", host,
                "short_message", shortMessage,
                "level", severity.value(),
                "timestamp", Seconds.sinceEpoch(time));
        gelf.put("_data", data);
        return gelf;
    }

    static GelfClient noOp() {
        return (String host, String shortMessage, Option<Date> timestamp, Severity severity, Map<String, Object> data) -> {};
    }

}
