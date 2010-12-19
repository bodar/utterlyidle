package com.googlecode.utterlyidle.proxy;

import com.googlecode.utterlyidle.Redirect;
import com.googlecode.utterlyidle.RequestGenerator;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

import static com.googlecode.totallylazy.proxy.Proxy.createProxy;
import static com.googlecode.utterlyidle.SeeOther.seeOther;

public class Resource implements InvocationHandler {
    public static ThreadLocalRedirect redirect = new ThreadLocalRedirect();

    private Resource() {}

    public static <T> T resource(Class<T> aCLass){
         return createProxy(aCLass, new Resource());
    }

    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        redirect.set(seeOther(getUrl(method, arguments)));
        return null;
    }

    public static <S> Redirect redirect(S value){
        return redirect.get();
    }

    public static <S> String urlOf(S value){
        return redirect.get().location();
    }

    public static String getUrl(Method method, Object[] arguments) {
        return new RequestGenerator(method).generate(arguments).url().toString();
    }
}