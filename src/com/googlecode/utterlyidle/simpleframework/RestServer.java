package com.googlecode.utterlyidle.simpleframework;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.RestApplication;
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

public class RestServer {
    private Connection connection;

    public RestServer(int port, BasePath basePath, Application applcation) throws IOException {
        Container container = new RestContainer(applcation.add(new RequestInstanceModule(basePath)));
        connection = new SocketConnection(container);
        SocketAddress address = new InetSocketAddress(port);
        connection.connect(address);
    }

    public void stop() throws Exception {
        connection.close();
    }

    public static void main(String[] args) throws IOException {
        new Test();
    }

    public static class Test extends RestServer{
        public Test() throws IOException {
            super(8000, basePath("/"), new RestApplication().add(new SingleResourceModule(HelloWorld.class)));
        }
    }
}
