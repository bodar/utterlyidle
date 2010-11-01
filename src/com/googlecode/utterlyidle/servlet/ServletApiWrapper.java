package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.AttributeMap;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.WebRoot;
import com.googlecode.utterlyidle.io.Url;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import static com.googlecode.utterlyidle.io.Url.url;

public class ServletApiWrapper {
    public static Request request(HttpServletRequest request) {
        try {
            return new ServletRequest(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Callable<WebRoot> webRoot(final ServletContext context) {
        return new Callable<WebRoot>() {
            public WebRoot call() throws Exception {
                try {
                    final URL url1 = context.getResource("/WEB-INF/web.xml");
                    Url url = url(url1).parent().parent();
                    return WebRoot.webRoot(url);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Callable<AttributeMap> attributeMap(final ServletContext context) {
        return new Callable<AttributeMap>() {
            public AttributeMap call() throws Exception {
                return new AttributeMap() {
                    @Override
                    public AttributeMap put(String name, Object value) {
                        context.setAttribute(name, value);
                        return this;
                    }

                    @Override
                    public <T> T get(String name, Class<T> aClass) {
                        return aClass.cast(context.getAttribute(name));
                    }
                };
            }
        };
    }

    public static Response response(final HttpServletResponse response) {
        try {
            return new Response(response.getOutputStream()) {
                @Override
                public Response header(String name, String value) {
                    response.setHeader(name, value);
                    super.header(name, value);
                    return this;
                }

                @Override
                public Response code(Status value) {
                    response.setStatus(value.code());
                    super.code(value);
                    return this;
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
