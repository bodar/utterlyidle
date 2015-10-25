package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.httpserver.RestServer;
import com.googlecode.utterlyidle.ssl.SSL;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.totallylazy.Uri.uri;
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

    private final BasePath basePath;
    private final int maxThreadNumber;
    private final InetAddress bindAddress;
    private final int port;
    private final Class<? extends Server> serverClass;
    private final Option<SSLContext> sslContext;

    public ServerConfiguration(BasePath basePath, int maxThreadNumber, InetAddress bindAddress, int port, Class<? extends Server> serverClass, final Option<SSLContext> sslContext) {
        this.basePath = basePath;
        this.maxThreadNumber = maxThreadNumber;
        this.bindAddress = bindAddress;
        this.port = port;
        this.serverClass = serverClass;
        this.sslContext = sslContext;
    }

    public ServerConfiguration() {
        this(new BasePath(DEFAULT_BASE_PATH), Integer.parseInt(DEFAULT_THREAD_NUMBER), toInetAddress(DEFAULT_BIND_ADDRESS), Integer.parseInt(DEFAULT_PORT), toServer(DEFAULT_CLASS), none(SSLContext.class));
    }

    public ServerConfiguration(Properties properties) {
        this(
                new BasePath(properties.getProperty(SERVER_BASE_PATH, DEFAULT_BASE_PATH)),
                valueOf(properties.getProperty(MAX_THREAD_NUM, DEFAULT_THREAD_NUMBER)),
                toInetAddress(properties.getProperty(SERVER_BIND_ADDRESS, DEFAULT_BIND_ADDRESS)),
                valueOf(properties.getProperty(SERVER_PORT, DEFAULT_PORT)),
                toServer(properties.getProperty(SERVER_CLASS, DEFAULT_CLASS)),
                toSSLContext(properties.getProperty(SERVER_PROTOCOL, DEFAULT_PROTOCOL)));
    }

    public static ServerConfiguration defaultConfiguration() {
        return new ServerConfiguration();
    }

    public BasePath basePath() {
        return basePath;
    }

    public ServerConfiguration basePath(BasePath basePath) {
        return new ServerConfiguration(basePath, maxThreadNumber, bindAddress, port, serverClass, sslContext);
    }

    public int maxThreadNumber() {
        return maxThreadNumber;
    }

    public ServerConfiguration maxThreadNumber(int maxThreadNumber) {
        return new ServerConfiguration(basePath, maxThreadNumber, bindAddress, port, serverClass, sslContext);
    }

    public Class<? extends Server> serverClass() {
        return serverClass;
    }

    public ServerConfiguration serverClass(Class<? extends Server> serverClass) {
        return new ServerConfiguration(basePath, maxThreadNumber, bindAddress, port, serverClass, sslContext);
    }

    public InetAddress bindAddress() {
        return bindAddress;
    }

    public ServerConfiguration bindAddress(InetAddress bindAddress) {
        return new ServerConfiguration(basePath, maxThreadNumber, bindAddress, port, serverClass, sslContext);
    }

    public int port() {
        return port;
    }

    public ServerConfiguration port(int bindPort) {
        return new ServerConfiguration(basePath, maxThreadNumber, bindAddress, bindPort, serverClass, sslContext);
    }

    public String protocol() {
        return sslContext.isDefined() ? Protocol.HTTPS : Protocol.HTTP;
    }

    public ServerConfiguration protocol(final String protocol) {
        return new ServerConfiguration(basePath, maxThreadNumber, bindAddress, port, serverClass, toSSLContext(protocol));
    }

    public Option<SSLContext> sslContext() {
        return sslContext;
    }

    public ServerConfiguration sslContext(final SSLContext sslContext) {
        return new ServerConfiguration(basePath, maxThreadNumber, bindAddress, port, serverClass, some(sslContext));
    }

    private static Option<SSLContext> toSSLContext(String protocol){
        if(protocol.equals(Protocol.HTTPS)) return some(SSL.sslContext());
        return none();
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