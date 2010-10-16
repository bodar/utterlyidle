package com.googlecode.utterlyidle;


import com.googlecode.totallylazy.proxy.IgnoreConstructorsEnhancer;
import com.googlecode.utterlyidle.servlet.BasePath;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.lang.reflect.Method;

public class Redirect {
    private final String location;

    private Redirect(String location) {
        this.location = location;
    }

    public String location() {
        return location;
    }

    public void applyTo(BasePath base, Response response) {
        response.header(HttpHeaders.LOCATION, base + "/" + location);
        response.code(Status.SEE_OTHER);
    }

    public static Redirect redirect(StreamingOutput path) throws IOException {
        OutputStream output = new ByteArrayOutputStream();
        path.write(output);
        return redirect(output.toString());
    }

    public static Redirect redirect(StreamingWriter path) {
        try {
            Writer output = new StringWriter();
            path.write(output);
            return redirect(output.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Redirect redirect(String location) {
        return new Redirect(location);
    }


    public static <T> T resource(Class<T> aClass) {
        Enhancer enhancer = new IgnoreConstructorsEnhancer();
        enhancer.setSuperclass(aClass);
        enhancer.setCallback(new ResourcePath());
        return (T) enhancer.create();
    }

    public static String getPath(Method method, Object[] arguments) {
        UriTemplate uriTemplate = new UriTemplateExtractor().extract(method);
        RequestGenerator requestGenerator = new RequestGenerator(uriTemplate, method);
        return requestGenerator.generate(arguments).path();
    }

    static class ResourcePath implements MethodInterceptor {
        public Object intercept(Object o, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable {
            return createReturnType(method.getReturnType(), getPath(method, arguments));
        }

        private Object createReturnType(Class returnType, final String path) {
            if (returnType == StreamingOutput.class) {
                return new StreamingOutput() {
                    public void write(OutputStream output) throws IOException, WebApplicationException {
                        Writer writer = new OutputStreamWriter(output);
                        writer.write(path);
                        writer.flush();
                    }
                };
            }

            if (returnType == StreamingWriter.class) {
                return new StreamingWriter() {
                    public void write(Writer writer) {
                        try {
                            writer.write(path);
                        } catch (IOException e) {
                            throw new UnsupportedOperationException(e);
                        }
                    }
                };
            }

            return path;
        }
    }
}