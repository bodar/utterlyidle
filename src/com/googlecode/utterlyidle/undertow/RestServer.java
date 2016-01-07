package com.googlecode.utterlyidle.undertow;

import com.googlecode.totallylazy.Classes;
import com.googlecode.totallylazy.Fields;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Lists;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Uri;
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

import static com.googlecode.totallylazy.Fields.access;
import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.option;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
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
                .map(listValueOf(server))
                .flatMap(new Function1<List<Object>, Option<Integer>>() {
                    @Override
                    public Option<Integer> call(final List<Object> channels) throws Exception {
                        return sequence(channels)
                                .flatMap(portFrom())
                                .headOption();
                    }
                })
                .getOrThrow(new IllegalStateException("Cannot find port from Undertow using reflection!"));
    }

    private static Class<?> sslChannel = Classes.forName("io.undertow.protocols.ssl.UndertowAcceptingSslChannel").get();

    private Option<Integer> portFrom(Object channel) throws Exception {
        if(sslChannel.isInstance(channel)) {
            channel = declaredField(channel, "tcpServer").map(Fields.value(channel)).get();
        }
        return socket(channel);
    }

    private Function1<? super Object, Option<Integer>> portFrom() {
        return new Function1<Object, Option<Integer>>() {
            @Override
            public Option<Integer> call(final Object channel) throws Exception {
                return portFrom(channel);
            }
        };
    }

    private Option<Integer> socket(final Object channel) throws IllegalAccessException {
        return declaredField(channel, "socket")
                .map(new Function1<Field, Integer>() {
                    @Override
                    public Integer call(final Field socketField) throws Exception {
                        final ServerSocket serverSocket = (ServerSocket) socketField.get(channel);
                        return serverSocket.getLocalPort();
                    }
                });
    }

    private List<Object> listValueOf(Field field, Object object) {
        try {
            return Fields.get(field, object);
        } catch (IllegalAccessException e) {
            return Lists.list();
        }
    }

    private Function1<? super Field, List<Object>> listValueOf(final Undertow server) {
        return new Function1<Field, List<Object>>() {
            @Override
            public List<Object> call(final Field field) throws Exception {
                return listValueOf(field, server);
            }
        };
    }

    private Option<Field> declaredField(Object object, String fieldName) {
        try {
            return option(access(object.getClass().getDeclaredField(fieldName)));
        } catch (NoSuchFieldException e) {
            return none();
        }
    }

}
