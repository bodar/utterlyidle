package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.io.Url;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import static com.googlecode.utterlyidle.io.Url.url;

public class WarRoot {
    private final Url url;

    public WarRoot(Url url) {
        this.url = url;
    }

    public static Callable<WarRoot> warRoot(final ServletContext context) {
        return new Callable<WarRoot>() {
            public WarRoot call() throws Exception {
                try {
                    final URL url1 = context.getResource("/WEB-INF/web.xml");
                    Url url = url(url1).parent().parent();
                    return new WarRoot(url);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}

