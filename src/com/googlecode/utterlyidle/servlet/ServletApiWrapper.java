package com.googlecode.utterlyidle.servlet;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.io.Url;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.io.Url.url;

public class ServletApiWrapper {
    public static Request request(HttpServletRequest request) {
        try {
            return new RequestWithServletStuff(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BasePath basePath(HttpServletRequest request) {
        return BasePath.basePath(request.getContextPath() + request.getServletPath());
    }

    public static ResourcePath resourcePath(HttpServletRequest request) {
        return ResourcePath.resourcePath(request.getPathInfo());
    }

    public static HeaderParameters headerParameters(final HttpServletRequest request) {
        return (HeaderParameters) Sequences.<String>sequence(request.getHeaderNames()).foldLeft(new HeaderParameters(), new Callable2<HeaderParameters, String, HeaderParameters>() {
            public HeaderParameters call(HeaderParameters result, final String name) throws Exception {
                return (HeaderParameters) Sequences.<String>sequence(request.getHeaders(name)).foldLeft(result, Parameters.<HeaderParameters>addParameter(name));
            }
        });
    }

    public static QueryParameters queryParameters(final HttpServletRequest request) {
        return (QueryParameters) Sequences.<String>sequence(request.getParameterNames()).foldLeft(new QueryParameters(), ServletApiWrapper.<QueryParameters>addParameters(request));
    }

    public static FormParameters formParameters(final HttpServletRequest request) {
        return (FormParameters) Sequences.<String>sequence(request.getParameterNames()).foldLeft(new FormParameters(), ServletApiWrapper.<FormParameters>addParameters(request));
    }

    public static <T extends Parameters> Callable2<T, String, T> addParameters(final HttpServletRequest request) {
        return new Callable2<T, String, T>() {
            public T call(T result, final String name) throws Exception {
                return sequence(request.getParameterValues(name)).foldLeft(result, Parameters.<T>addParameter(name));
            }
        };
    }

    public static ApplicationPath applicationPath(HttpServletRequest request) {
        return ApplicationPath.applicationPath(request.getContextPath() + request.getServletPath());
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
