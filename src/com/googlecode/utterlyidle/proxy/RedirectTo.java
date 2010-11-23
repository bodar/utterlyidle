package com.googlecode.utterlyidle.proxy;

import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Redirect;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import net.sf.cglib.proxy.InvocationHandler;

import javax.ws.rs.core.HttpHeaders;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.proxy.Generics.getGenericSuperclassType;
import static com.googlecode.totallylazy.proxy.Proxy.createProxy;

public abstract class RedirectTo<T> implements Redirect, InvocationHandler{
    private String location;
    protected final T call;

    protected RedirectTo() {
        Class<T> aClass = getGenericSuperclassType(this.getClass(), 0);
        call = createProxy(aClass, this);
    }

    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        location = Resource.getUrl(method, arguments);
        return null;
    }

    public String location() {
        return location;
    }

    public void applyTo(BasePath base, Response response) {
        response.header(HttpHeaders.LOCATION, base.file(location).toString());
        response.code(Status.SEE_OTHER);
    }
}
