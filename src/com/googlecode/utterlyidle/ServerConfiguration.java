package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.httpserver.RestServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.totallylazy.io.Uri.uri;
import static java.lang.Integer.valueOf;
import static java.lang.String.format;

public class ServerConfiguration {
    public static final String SERVER_PROTOCOL = "server.protocol";
    public static final String SERVER_BASE_PATH = "server.base.path";
    public static final String MAX_THREAD_NUM = "server.max.thread.number";
    public static final String SERVER_BIND_ADDRESS = "server.bind.address";
    public static final String SERVER_PORT = "server.port";
    public static final String SERVER_CLASS = "server.class";

    public static final String DEFAULT_PROTOCOL = Protocol.HTTP;
    public static final String DEFAULT_BASE_PATH = "/";
    public static final String DEFAULT_THREAD_NUMBER = "50";
    public static final String DEFAULT_BIND_ADDRESS = "0.0.0.0";
    public static final String DEFAULT_PORT = "0";
    public static final String DEFAULT_CLASS = RestServer.class.getCanonicalName();

    private final String protocol;
    private final BasePath basePath;
    private final int maxThreadNumber;
    private final InetAddress bindAddress;
    private final int port;
    private final Class<? extends Server> serverClass;

    public ServerConfiguration(String protocol, BasePath basePath, int maxThreadNumber, InetAddress bindAddress, int port, Class<? extends Server> serverClass) {
        this.protocol = protocol;
        this.basePath = basePath;
        this.maxThreadNumber = maxThreadNumber;
        this.bindAddress = bindAddress;
        this.port = port;
        this.serverClass = serverClass;
    }

    public ServerConfiguration() {
        this(DEFAULT_PROTOCOL, new BasePath(DEFAULT_BASE_PATH), Integer.parseInt(DEFAULT_THREAD_NUMBER), toInetAddress(DEFAULT_BIND_ADDRESS), Integer.parseInt(DEFAULT_PORT), toServer(DEFAULT_CLASS));
    }

    public ServerConfiguration(Properties properties) {
        this(properties.getProperty(SERVER_PROTOCOL, DEFAULT_PROTOCOL),
                new BasePath(properties.getProperty(SERVER_BASE_PATH, DEFAULT_BASE_PATH)),
                valueOf(properties.getProperty(MAX_THREAD_NUM, DEFAULT_THREAD_NUMBER)),
                toInetAddress(properties.getProperty(SERVER_BIND_ADDRESS, DEFAULT_BIND_ADDRESS)),
                valueOf(properties.getProperty(SERVER_PORT, DEFAULT_PORT)),
                toServer(properties.getProperty(SERVER_CLASS, DEFAULT_CLASS)));
    }

    public static ServerConfiguration defaultConfiguration() {
        return new ServerConfiguration();
    }

    public BasePath basePath() {
        return basePath;
    }

    public ServerConfiguration basePath(BasePath basePath) {
        return new ServerConfiguration(protocol, basePath, maxThreadNumber, bindAddress, port, serverClass);
    }

    public int maxThreadNumber() {
        return maxThreadNumber;
    }

    public ServerConfiguration maxThreadNumber(int maxThreadNumber) {
        return new ServerConfiguration(protocol, basePath, maxThreadNumber, bindAddress, port, serverClass);
    }

    public Class<? extends Server> serverClass() {
        return serverClass;
    }

    public ServerConfiguration serverClass(Class<? extends Server> serverClass) {
        return new ServerConfiguration(protocol, basePath, maxThreadNumber, bindAddress, port, serverClass);
    }

    public InetAddress bindAddress() {
        return bindAddress;
    }

    public ServerConfiguration bindAddress(InetAddress bindAddress) {
        return new ServerConfiguration(protocol, basePath, maxThreadNumber, bindAddress, port, serverClass);
    }

    public int port() {
        return port;
    }

    public ServerConfiguration port(int bindPort) {
        return new ServerConfiguration(protocol, basePath, maxThreadNumber, bindAddress, bindPort, serverClass);
    }

    public String protocol() {
        return protocol;
    }

    public ServerConfiguration protocol(final String protocol) {
        return new ServerConfiguration(protocol, basePath, maxThreadNumber, bindAddress, port, serverClass);
    }

    private static InetAddress toInetAddress(final String address) {
        try {
            return InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<? extends Server> toServer(String className) {
        try {
            return cast(Class.forName(className));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Uri toUrl() {
        return uri(format("%s://localhost:%s%s", protocol(), port(), basePath()));
    }
}