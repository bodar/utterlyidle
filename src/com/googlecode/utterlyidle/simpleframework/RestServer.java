package com.googlecode.utterlyidle.simpleframework;

import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.RestApplication;
import com.googlecode.utterlyidle.httpserver.HelloWorld;
import com.googlecode.utterlyidle.modules.SingleResourceModule;

public class RestServer {
    private Connection connection;

    public RestServer(int port, Application applcation) throws IOException {
        Container container = new RestContainer(applcation);
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
            super(8000, new RestApplication().add(new SingleResourceModule(HelloWorld.class)));
        }
    }
}
