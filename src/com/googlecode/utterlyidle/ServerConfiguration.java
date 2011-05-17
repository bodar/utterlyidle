package com.googlecode.utterlyidle;

import java.util.Properties;

import static java.lang.Integer.valueOf;

public class ServerConfiguration {

    public static final String SERVER_URL = "server.url";
    public static final String MAX_THREAD_NUM = "server.max.thread.number";
    public static final int DEFAULT_THREAD_NUMBER = 50;
    public static final String DEFAULT_SERVER_URL = String.format("http://%s:%s/", anyLocalAddress(), randomPort());

    private ServerUrl serverUrl = new ServerUrl(DEFAULT_SERVER_URL);

    private int maxThreadNumber = DEFAULT_THREAD_NUMBER;
    private ServerConfiguration() {}

    public static ServerConfiguration serverConfiguration() {
        return new ServerConfiguration();
    }

    public static ServerConfiguration serverConfiguration(Properties properties) {
        return initialize(new ServerConfiguration(), properties);
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
        return serverConfiguration;
    }

    private static int randomPort() {
        return 0;
    }

    private static String anyLocalAddress() {
        return "localhost";
    }
}
