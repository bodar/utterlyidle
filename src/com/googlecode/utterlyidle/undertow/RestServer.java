package com.googlecode.utterlyidle.undertow;

import com.googlecode.totallylazy.Classes;
import com.googlecode.totallylazy.Lists;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.totallylazy.reflection.Fields;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.ApplicationBuilder;
import com.googlecode.utterlyidle.Protocol;
import com.googlecode.utterlyidle.ServerConfiguration;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.services.Service;
import io.undertow.Undertow;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.util.List;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.option;
import static com.googlecode.totallylazy.functions.Time0.calculateMilliseconds;
import static com.googlecode.totallylazy.reflection.Fields.access;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static java.lang.String.format;
import static java.lang.System.nanoTime;

public class RestServer implements com.googlecode.utterlyidle.Server {
    private final Application application;
    private final Undertow server;

    private Uri uri;

    public RestServer(Application application, ServerConfiguration configuration) throws Exception {
        this.application = application;

        server = startApp(application, configuration);
    }

    @Override
    public Application application() {
        return application;
    }

    @Override
    public Uri uri() {
        return uri;
    }

    @Override
    public void close() throws IOException {
        server.stop();
    }

    public static void main(String[] args) throws Exception {
        ApplicationBuilder.application(HelloWorldApplication.class).start(defaultConfiguration().port(8002));
    }

    private Undertow startApp(Application application, ServerConfiguration configuration) throws Exception {
        long start = nanoTime();
        Undertow server = startUpServer(application, configuration);
        System.out.println(format("Listening on %s, started Undertow in %s msecs", uri, calculateMilliseconds(start, nanoTime())));
        Service.functions.start().callConcurrently(this.application);
        return server;
    }

    private Undertow startUpServer(Application application, ServerConfiguration configuration) throws Exception {
        Undertow.Builder builder = builder(configuration);

        Undertow server = builder
                .setWorkerThreads(configuration.maxThreadNumber())
                .setHandler(new RestHttpHandler(application))
                .build();
        server.start();

        uri = configuration.port(findPortInUse(server)).toUrl();

        return server;
    }

    private Undertow.Builder builder(final ServerConfiguration configuration) {
        if(configuration.protocol().equals(Protocol.HTTPS)) {
            return Undertow.builder().addHttpsListener(configuration.port(), configuration.bindAddress().getHostAddress(), configuration.sslContext().get());
        }
        return Undertow.builder().addHttpListener(configuration.port(), configuration.bindAddress().getHostAddress());
    }

    private int findPortInUse(Undertow server) {
        return declaredField(server, "channels")
                .map(field -> listValueOf(field, server))
                .map(Sequences::sequence)
                .flatMap(channels -> channels
                        .flatMap(this::portFrom)
                        .headOption())
                .getOrThrow(new IllegalStateException("Cannot find port from Undertow using reflection!"));
    }

    private static Class<?> sslChannel = Classes.forName("io.undertow.protocols.ssl.UndertowAcceptingSslChannel").get();
    private Option<Integer> portFrom(Object channel) throws Exception {
        if(sslChannel.isInstance(channel)) {
            channel = declaredField(channel, "tcpServer").map(Fields.value(channel)).get();
        }
        return socket(channel);
    }

    private Option<Integer> socket(final Object channel) throws IllegalAccessException {
        return declaredField(channel, "socket")
                .map(socketField -> (ServerSocket) socketField.get(channel))
                .map(ServerSocket::getLocalPort);
    }

    private List<Object> listValueOf(Field field, Object object) {
        try {
            return Fields.get(field, object);
        } catch (IllegalAccessException e) {
            return Lists.list();
        }
    }

    private Option<Field> declaredField(Object object, String fieldName) {
        try {
            return option(access(object.getClass().getDeclaredField(fieldName)));
        } catch (NoSuchFieldException e) {
            return none();
        }
    }

}
