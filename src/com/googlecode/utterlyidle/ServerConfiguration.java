package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.httpserver.RestServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import static java.lang.Integer.valueOf;
import static java.lang.String.format;

public class ServerConfiguration {

    public static final String SERVER_URL = "server.url";
    public static final String SERVER_TYPE = "server.type";
    public static final String SERVER_BIND_ADDRESS = "server.bind.address";
    public static final String MAX_THREAD_NUM = "server.max.thread.number";

    public static final int DEFAULT_THREAD_NUMBER = 50;
    public static final String DEFAULT_SERVER_URL = format("http://localhost:%s/", randomPort());
    public static final String DEFAULT_SERVER = RestServer.class.getCanonicalName();
    public static final String ALL_LOCAL_ADDRESSES = "0.0.0.0";

    private ServerUrl serverUrl = new ServerUrl(DEFAULT_SERVER_URL);
    private int maxThreadNumber = DEFAULT_THREAD_NUMBER;
    private InetAddress bindAddress = toInetAddress(ALL_LOCAL_ADDRESSES);
    private Class<? extends Server> serverClass = toServer(DEFAULT_SERVER);

    private ServerConfiguration() {
    }

    public static ServerConfiguration defaultConfiguration() {
        return new ServerConfiguration();
    }

    public static ServerConfiguration serverConfiguration(Properties properties) {
        return initialize(new ServerConfiguration(), properties);
    }

    public Class<? extends Server> serverClass() {
        return serverClass;
    }

    public ServerConfiguration serverUrl(ServerUrl serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public ServerConfiguration maxThreadNumber(int maxThreadNumber) {
        this.maxThreadNumber = maxThreadNumber;
        return this;
    }

    public ServerConfiguration port(int portNumber) {
        serverUrl = serverUrl().replacePort(portNumber);
        return this;
    }

    public ServerUrl serverUrl() {
        return serverUrl;
    }

    public int maxThreadNumber() {
        return maxThreadNumber;
    }

    private static ServerConfiguration initialize(ServerConfiguration serverConfiguration, Properties properties) {
        serverConfiguration.maxThreadNumber(valueOf(properties.getProperty(MAX_THREAD_NUM, String.valueOf(DEFAULT_THREAD_NUMBER))));
        serverConfiguration.serverUrl(new ServerUrl(properties.getProperty(SERVER_URL, DEFAULT_SERVER_URL)));
        serverConfiguration.bindAddress(toInetAddress(properties.getProperty(SERVER_BIND_ADDRESS, ALL_LOCAL_ADDRESSES)));
        serverConfiguration.serverClass(toServer(properties.getProperty(SERVER_TYPE, ALL_LOCAL_ADDRESSES)));
        return serverConfiguration;
    }

    public ServerConfiguration serverClass(Class<? extends Server> aClass) {
        serverClass = aClass;
        return this;
    }

    private ServerConfiguration bindAddress(InetAddress address) {
        this.bindAddress = address;
        return this;
    }

    private static int randomPort() {
        return 0;
    }

    public InetAddress bindAddress() {
        return bindAddress;
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
