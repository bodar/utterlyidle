package com.googlecode.utterlyidle.proxy;

import com.googlecode.utterlyidle.RequestGenerator;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Responses;
import net.sf.cglib.proxy.InvocationHandler;

import javax.ws.rs.core.HttpHeaders;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.proxy.Proxy.createProxy;

public class Resource implements InvocationHandler {
    public static ThreadLocalRedirect redirect = new ThreadLocalRedirect();

    private Resource() {}

    public static <T> T resource(Class<T> aCLass){
         return createProxy(aCLass, new Resource());
    }

    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        redirect.set(getUrl(method, arguments));
        return null;
    }

    public static <S> Response redirect(S value){
        return Responses.seeOther(redirect.get());
    }

    public static <S> String urlOf(S value){
        return redirect.get();
    }

    public static String location(Response response) {
        return response.header(HttpHeaders.LOCATION);
    }

    public static String getUrl(Method method, Object[] arguments) {
        return new RequestGenerator(method).generate(arguments).url().toString();
    }
}
