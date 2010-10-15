package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.io.Url;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;

import static com.googlecode.utterlyidle.io.Url.url;

public class WarRoot {
    private final Url url;

    public WarRoot(Url url) {
        this.url = url;
    }

    public static WarRoot warRoot(ServletContext context) {
        try {
            final URL url1 = context.getResource("/WEB-INF/web.xml");
            Url url = url(url1).parent().parent();
            return new WarRoot(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}

