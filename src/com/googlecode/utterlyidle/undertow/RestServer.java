package com.googlecode.utterlyidle.undertow;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.ApplicationBuilder;
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
        Undertow server = Undertow.builder()
                .addHttpListener(configuration.port(), configuration.bindAddress().getHostAddress())
                .setWorkerThreads(configuration.maxThreadNumber())
                .setHandler(new RestHttpHandler(application))
                .build();
        server.start();

        uri = configuration.port(findPortInUse(server)).toUrl();

        return server;
    }

    private int findPortInUse(Undertow server) {
        for (Object channel : listValueOf(declaredField(server, "channels"), server)) {
            Option<Integer> port = portFrom(channel);
            if (port.isDefined()) {
                return port.get();
            }
        }
        throw new IllegalStateException("Cannot find port from Undertow");
    }

    private Option<Integer> portFrom(Object channel) {
        Option<Field> socketField = declaredField(channel, "socket");
        if (socketField.isDefined()) {
            try {
                return option(((ServerSocket) socketField.get().get(channel)).getLocalPort());
            } catch (IllegalAccessException ignored) {
                // ignored
            }
        }
        return none();
    }

    @SuppressWarnings("unchecked")
    private Sequence<Object> listValueOf(Option<Field> field, Object object) {
        try {
            if (field.isDefined()) {
                return sequence((List<Object>) field.get().get(object));
            }
        } catch (IllegalAccessException ignored) {
            // ignored
        }
        return Sequences.empty();
    }

    private Option<Field> declaredField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return option(field);
        } catch (NoSuchFieldException e) {
            return none();
        }
    }

}
