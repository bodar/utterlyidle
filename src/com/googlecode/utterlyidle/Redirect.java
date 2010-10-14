package com.googlecode.utterlyidle;


import com.googlecode.utterlyidle.servlet.BasePath;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Redirect {
    private final String location;

    public Redirect(String location) {
        this.location = location;
    }

    public void applyTo(BasePath base, Response response) {
        response.setHeader(HttpHeaders.LOCATION, base + "/" + location);
        response.setCode(Status.SEE_OTHER);
    }

    public Redirect redirect(StreamingOutput path) throws IOException {
        OutputStream output = new ByteArrayOutputStream();
        path.write(output);
        return new Redirect(output.toString());
    }

    public Redirect redirect(StreamingWriter path){
        Writer output = new StringWriter();
        path.write(output);
        return new Redirect(output.toString());
    }

    public <T> T resource(Class<T> aClass)
    {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(aClass);
        enhancer.setCallback(new ResourcePath());
        Constructor constructorWithLeastArguments = aClass.getConstructors()[0];
        Class[] argumentTypes = constructorWithLeastArguments.getParameterTypes();
        return null;
    }

    public static String getPath(Method method, Object[] arguments){
        UriTemplate uriTemplate = new UriTemplateExtractor().extract(method);
        RequestGenerator requestGenerator = new RequestGenerator(uriTemplate, method);
        return requestGenerator.generate(arguments).path();
    }

    public static Object createReturnType(Class returnType, final String path){
        if (returnType == StreamingOutput.class) {
            return new StreamingOutput()
            {
                public void write(OutputStream output) throws IOException, WebApplicationException {
                    Writer writer = new OutputStreamWriter(output);
                    writer.write(path);
                    writer.flush();
                }
            };
        }

        if (returnType == StreamingWriter.class) {
            return new StreamingWriter()
            {
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

    static class ResourcePath implements MethodInterceptor {
        public Object intercept(Object o, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable {
                return createReturnType(method.getReturnType(), getPath(method, arguments));
        }
    }
}