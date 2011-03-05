package com.googlecode.utterlyidle.simpleframework;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.RestApplication;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.httpserver.HelloWorld;
import com.googlecode.utterlyidle.modules.RequestInstanceModule;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static com.googlecode.utterlyidle.BasePath.basePath;

public class RestServer implements Server {
    private Connection connection;
    private SocketAddress address;

    public RestServer(int port, BasePath basePath, Application applcation) throws Exception {
        Container container = new RestContainer(applcation.add(new RequestInstanceModule(basePath)));
        connection = new SocketConnection(container);
        address = new InetSocketAddress(port);
        start();
    }

    public Server start() throws Exception {
        connection.connect(address);
        return this;
    }

    public Server stop() throws IOException {
        connection.close();
        return this;
    }

    public static void main(String[] args) throws Exception {
        new Test();
    }

    public static class Test extends RestServer {
        public Test() throws Exception {
            super(8000, basePath("/"), new RestApplication().add(new SingleResourceModule(HelloWorld.class)));
        }
    }
}
