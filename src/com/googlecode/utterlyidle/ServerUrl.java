package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.io.Url;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import static java.net.URLDecoder.*;

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
        try {
            return serverUrl(new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), port, uri.getPath(), uri.getQuery(), uri.getRawFragment()).toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Callable1<? super String, String> changeToAbsoluteUrl(final ServerUrl serverUrl) {
        return new Callable1<String, String>() {
            public String call(String path) throws Exception {
                if(url(path).isAbsolute()) {
                    return path;
                }
                return decode(serverUrl.replacePath(serverUrl.path().file(path)).toURI().toURL().toString(), "UTF-8");
            }
        };
    }
}