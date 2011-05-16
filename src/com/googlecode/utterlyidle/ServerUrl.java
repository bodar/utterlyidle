package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.io.Url;

import java.net.URI;

import static java.net.URLDecoder.decode;

public class ServerUrl extends Url {
    public static ServerUrl serverUrl(String url) {
        return new ServerUrl(url);
    }

    protected ServerUrl(String url) {
        super(url);
    }

    public int port() {
        return toURI().getPort();
    }

    public String host() {
        return toURI().getHost();
    }

    public ServerUrl replacePort(int port) {
        URI uri = toURI();
        return serverUrl(toString(uri.getScheme(), uri.getRawUserInfo(), uri.getHost(), port, uri.getRawPath(), uri.getRawQuery(), uri.getRawFragment()));
    }

    public static Callable1<? super String, String> changeToAbsoluteUrl(final ServerUrl serverUrl) {
        return new Callable1<String, String>() {
            public String call(String path) throws Exception {
                if (url(path).isAbsolute()) {
                    return path;
                }
                return serverUrl.replacePath(serverUrl.path().file(path)).toString();
            }
        };
    }
}