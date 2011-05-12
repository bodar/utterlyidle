package com.googlecode.utterlyidle;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import static com.googlecode.utterlyidle.BasePath.basePath;
import static java.lang.Integer.valueOf;
import static java.net.InetAddress.getByName;

public class ServerConfiguration {

    public static final String PORT_NUMBER = "server.port.number";
    public static final String BASE_PATH = "server.base.path";
    public static final String MAX_THREAD_NUM = "server.max.thread.number";
    public static final String BIND_ADDRESS = "server.bind.address";

    private int portNumber = 0;
    private BasePath basePath = new BasePath("/");
    private int maxThreadNumber = 50;
    private InetAddress bindAddress = anyLocalAddress();

    private ServerConfiguration(){
    }

    public static ServerConfiguration serverConfiguration() {
        return new ServerConfiguration();
    }

    public static ServerConfiguration serverConfiguration(Properties properties) {
        return initialize(new ServerConfiguration(), properties);
    }

    private ServerConfiguration withBindAddress(InetAddress bindAddress) {
        this.bindAddress = bindAddress;
        return this;
    }

    public ServerConfiguration withPortNumber(int portNumber) {
        this.portNumber = portNumber;
        return this;
    }

    public ServerConfiguration withBasePath(BasePath basePath) {
        this.basePath = basePath;
        return this;
    }

    public ServerConfiguration withMaxThreadNumber(int maxThreadNumber) {
        this.maxThreadNumber = maxThreadNumber;
        return this;
    }

    public int portNumber() {
        return portNumber;
    }

    public BasePath basePath() {
        return basePath;
    }

    public int maxThreadNumber() {
        return maxThreadNumber;
    }

    public InetAddress bindAddress() {
        return bindAddress;
    }

    private static ServerConfiguration initialize(ServerConfiguration serverConfiguration, Properties properties) {
        if(properties.getProperty(PORT_NUMBER) != null) {
            serverConfiguration.withPortNumber(valueOf(properties.getProperty(PORT_NUMBER)));
        }

        if(properties.getProperty(MAX_THREAD_NUM) != null) {
            serverConfiguration.withMaxThreadNumber(valueOf(properties.getProperty(MAX_THREAD_NUM)));
        }

        if(properties.getProperty(BASE_PATH) != null) {
            serverConfiguration.withBasePath(BasePath.basePath(properties.getProperty(BASE_PATH)));
        }

        if(properties.getProperty(BIND_ADDRESS) != null) {
            try {
                serverConfiguration.withBindAddress(getByName(properties.getProperty(BIND_ADDRESS)));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        return serverConfiguration;
    }

    private InetAddress anyLocalAddress() {
        try {
            return getByName("0.0.0.0");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
