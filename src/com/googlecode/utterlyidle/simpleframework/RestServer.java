package com.googlecode.utterlyidle.simpleframework;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.ApplicationBuilder;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.ServerConfiguration;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.services.Service;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.IOException;
import java.net.InetSocketAddress;

import static com.googlecode.totallylazy.functions.TimeCallable.calculateMilliseconds;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static java.lang.String.format;
import static java.lang.System.nanoTime;

public class RestServer implements Server {
    private final Application application;
    private final Connection connection;
    private Uri uri;

    public RestServer(final Application application, final ServerConfiguration configuration) throws Exception {
        this.application = application;
        connection = startApp(application, configuration);
    }

    public void close() throws IOException {
        connection.close();
    }

    public static void main(String[] args) throws Exception {
        ApplicationBuilder.application(HelloWorldApplication.class).start(defaultConfiguration().port(8000));
    }

    @Override
    public Application application() {
        return application;
    }

    public Uri uri() {
        return uri;
    }

    private Connection startApp(Application application, ServerConfiguration configuration) throws Exception {
        long start = nanoTime();
        SocketConnection connection1 = startUpApp(application, configuration);
        System.out.println(format("Listening on %s, started SimpleWeb in %s msecs", uri, calculateMilliseconds(start, nanoTime())));
        Service.functions.start().callConcurrently(application);
        return connection1;
    }

    private SocketConnection startUpApp(Application application, ServerConfiguration configuration) throws IOException {
        Container container = new RestContainer(application);
        SocketConnection connection = new SocketConnection(new ContainerServer(container, configuration.maxThreadNumber()));
        InetSocketAddress socketAddress = (InetSocketAddress) connection.connect(new InetSocketAddress(configuration.bindAddress(), configuration.port()));

        ServerConfiguration updatedConfiguration = configuration.port(socketAddress.getPort());
        uri = updatedConfiguration.toUrl();
        return connection;
    }

}
