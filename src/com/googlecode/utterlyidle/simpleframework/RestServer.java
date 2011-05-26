package com.googlecode.utterlyidle.simpleframework;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.ServerConfiguration;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.io.Url;
import com.googlecode.utterlyidle.modules.RequestInstanceModule;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.IOException;
import java.net.InetSocketAddress;

import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static java.lang.String.format;
import static java.lang.System.nanoTime;

public class RestServer implements Server {
    private final Connection connection;
    private Url url;

    public RestServer(final Application application, final ServerConfiguration configuration) throws Exception {
        connection = startApp(application, configuration);
    }

    public void close() throws IOException {
        connection.close();
    }

    public static void main(String[] args) throws Exception {
        new RestServer(new HelloWorldApplication(), ServerConfiguration.defaultConfiguration().port(8000));
    }

    public Url getUrl() {
        return url;
    }

    private Connection startApp(Application application, ServerConfiguration configuration) throws Exception {
        long start = nanoTime();
        SocketConnection connection1 = startUpApp(application, configuration);
        System.out.println(format("Listening on %s, started SimpleWeb in %s msecs", url, calculateMilliseconds(start, nanoTime())));
        return connection1;
    }

    private SocketConnection startUpApp(Application application, ServerConfiguration configuration) throws IOException {
        Container container = new RestContainer(application);
        SocketConnection connection = new SocketConnection(new ContainerServer(container, configuration.maxThreadNumber()));
        InetSocketAddress socketAddress = (InetSocketAddress) connection.connect(new InetSocketAddress(configuration.bindAddress(), configuration.serverUrl().port()));

        ServerConfiguration updatedConfiguration = configuration.port(socketAddress.getPort());
        application.add(new RequestInstanceModule(updatedConfiguration.serverUrl()));
        url = updatedConfiguration.serverUrl();
        return connection;
    }

}
