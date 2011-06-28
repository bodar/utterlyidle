package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.httpserver.RestServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import static java.lang.Integer.valueOf;
import static java.lang.String.format;

public class ServerConfiguration {

    public static final String SERVER_URL = "server.url";
    public static final String SERVER_CLASS = "server.class";
    public static final String SERVER_BIND_ADDRESS = "server.bind.address";
    public static final String MAX_THREAD_NUM = "server.max.thread.number";

    public static final String DEFAULT_THREAD_NUMBER = "50";
    public static final String DEFAULT_SERVER_URL = format("http://localhost:%s/", randomPort());
    public static final String DEFAULT_SERVER_CLASS = RestServer.class.getCanonicalName();
    public static final String DEFAULT_BIND_ADDRESS = "0.0.0.0";

    private final ServerUrl serverUrl;
    private final int maxThreadNumber;
    private final InetAddress bindAddress;
    private final Class<? extends Server> serverClass;

    public ServerConfiguration(ServerUrl serverUrl, int maxThreadNumber, InetAddress bindAddress, Class<? extends Server> serverClass) {
        this.serverUrl = serverUrl;
        this.maxThreadNumber = maxThreadNumber;
        this.bindAddress = bindAddress;
        this.serverClass = serverClass;
    }

    public ServerConfiguration() {
        this( new ServerUrl(DEFAULT_SERVER_URL), Integer.parseInt(DEFAULT_THREAD_NUMBER), toInetAddress(DEFAULT_BIND_ADDRESS), toServer(DEFAULT_SERVER_CLASS));
    }

    public ServerConfiguration(Properties properties) {
        this(new ServerUrl(properties.getProperty(SERVER_URL, DEFAULT_SERVER_URL)),
                valueOf(properties.getProperty(MAX_THREAD_NUM, DEFAULT_THREAD_NUMBER)),
                toInetAddress(properties.getProperty(SERVER_BIND_ADDRESS, DEFAULT_BIND_ADDRESS)),
                toServer(properties.getProperty(SERVER_CLASS, DEFAULT_SERVER_CLASS)));
    }

    public static ServerConfiguration defaultConfiguration() {
        return new ServerConfiguration();
    }

    public ServerUrl serverUrl() {
        return serverUrl;
    }

    public ServerConfiguration serverUrl(ServerUrl serverUrl) {
        return new ServerConfiguration(serverUrl, maxThreadNumber, bindAddress, serverClass);
    }

    public int maxThreadNumber() {
        return maxThreadNumber;
    }

    public ServerConfiguration maxThreadNumber(int maxThreadNumber) {
        return new ServerConfiguration(serverUrl, maxThreadNumber, bindAddress, serverClass);
    }

    public Class<? extends Server> serverClass() {
        return serverClass;
    }

    public ServerConfiguration serverClass(Class<? extends Server> serverClass) {
        return new ServerConfiguration(serverUrl, maxThreadNumber, bindAddress, serverClass);
    }

    public InetAddress bindAddress() {
        return bindAddress;
    }

    public ServerConfiguration bindAddress(InetAddress bindAddress) {
        return new ServerConfiguration(serverUrl, maxThreadNumber, bindAddress, serverClass);
    }

    public ServerConfiguration port(int portNumber) {
        return new ServerConfiguration(serverUrl.port(portNumber), maxThreadNumber, bindAddress, serverClass);
    }

    private static int randomPort() {
        return 0;
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
            return (Class<? extends Server>) Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
