package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Unchecked;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.httpserver.RestServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.totallylazy.Uri.uri;
import static java.lang.Integer.valueOf;
import static java.lang.String.format;

public class ServerConfiguration {

    public static final String SERVER_BASE_PATH = "server.base.path";
    public static final String MAX_THREAD_NUM = "server.max.thread.number";
    public static final String SERVER_BIND_ADDRESS = "server.bind.address";
    public static final String SERVER_PORT = "server.port";
    public static final String SERVER_CLASS = "server.class";

    public static final String DEFAULT_BASE_PATH = "/";
    public static final String DEFAULT_THREAD_NUMBER = "50";
    public static final String DEFAULT_BIND_ADDRESS = "0.0.0.0";
    public static final String DEFAULT_PORT = "0";
    public static final String DEFAULT_CLASS = RestServer.class.getCanonicalName();

    private final BasePath basePath;
    private final int maxThreadNumber;
    private final InetAddress bindAddress;
    private final int port;
    private final Class<? extends Server> serverClass;

    public ServerConfiguration(BasePath basePath, int maxThreadNumber, InetAddress bindAddress, int port, Class<? extends Server> serverClass) {
        this.basePath = basePath;
        this.maxThreadNumber = maxThreadNumber;
        this.bindAddress = bindAddress;
        this.port = port;
        this.serverClass = serverClass;
    }

    public ServerConfiguration() {
        this( new BasePath(DEFAULT_BASE_PATH), Integer.parseInt(DEFAULT_THREAD_NUMBER), toInetAddress(DEFAULT_BIND_ADDRESS), Integer.parseInt(DEFAULT_PORT), toServer(DEFAULT_CLASS));
    }

    public ServerConfiguration(Properties properties) {
        this(new BasePath(properties.getProperty(SERVER_BASE_PATH, DEFAULT_BASE_PATH)),
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
        return new ServerConfiguration(basePath, maxThreadNumber, bindAddress, port, serverClass);
    }

    public int maxThreadNumber() {
        return maxThreadNumber;
    }

    public ServerConfiguration maxThreadNumber(int maxThreadNumber) {
        return new ServerConfiguration(basePath, maxThreadNumber, bindAddress, port, serverClass);
    }

    public Class<? extends Server> serverClass() {
        return serverClass;
    }

    public ServerConfiguration serverClass(Class<? extends Server> serverClass) {
        return new ServerConfiguration(basePath, maxThreadNumber, bindAddress, port, serverClass);
    }

    public InetAddress bindAddress() {
        return bindAddress;
    }

    public ServerConfiguration bindAddress(InetAddress bindAddress) {
        return new ServerConfiguration(basePath, maxThreadNumber, bindAddress, port, serverClass);
    }

    public ServerConfiguration port(int bindPort) {
        return new ServerConfiguration(basePath, maxThreadNumber, bindAddress, bindPort, serverClass);
    }

    public int port() {
        return port;
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
        return uri(format("http://localhost:%s%s", port(), basePath()));
    }
}
