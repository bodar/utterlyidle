package com.googlecode.utterlyidle.simpleframework;

import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.httpserver.HelloWorld;
import com.googlecode.utterlyidle.io.Url;
import com.googlecode.utterlyidle.jetty.RestApplicationActivator;
import com.googlecode.utterlyidle.modules.RequestInstanceModule;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.io.Url.url;
import static java.lang.String.format;
import static java.lang.System.nanoTime;

public class RestServer implements Server {
    private final Connection connection;
    private final Closeable closableActivator;
    private Url url;

    public RestServer(int port, BasePath basePath, CloseableCallable<Application> applicationActivator) throws Exception {
        this.closableActivator = applicationActivator;
        connection = startApp(port, basePath, applicationActivator.call());
    }

    public RestServer(BasePath basePath, CloseableCallable<Application> applicationActivator) throws Exception {
        this(0, basePath, applicationActivator);
    }

    public void close() throws IOException {
        connection.close();
        closableActivator.close();
    }

    public static void main(String[] args) throws Exception {
        new Test();
    }

    public Url getUrl() {
        return url;
    }

    public static class Test extends RestServer {
        public Test() throws Exception {
            super(8000, basePath("/"), new RestApplicationActivator(new SingleResourceModule(HelloWorld.class)));
        }
    }

    private Connection startApp(int port, BasePath basePath, Application application) throws Exception {
        long start = nanoTime();
        Container container = new RestContainer(application.add(new RequestInstanceModule(basePath)));
        SocketConnection connection = new SocketConnection(container);
        InetSocketAddress socketAddress = (InetSocketAddress)connection.connect(new InetSocketAddress(port));
        url = url(format("http://localhost:%s%s", socketAddress.getPort(), basePath));
        System.out.println(format("Listening on %s, started SimpleWeb in %s msecs", url, calculateMilliseconds(start, nanoTime())));
        return connection;
    }

}
