package com.googlecode.utterlyidle.servlet;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.AttributeMap;
import com.googlecode.utterlyidle.WebRoot;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.io.HierarchicalPath.hierarchicalPath;

public class ServletApiWrapper {

    public static Callable<WebRoot> webRoot(final ServletContext context) {
        return new Callable<WebRoot>() {
            public WebRoot call() throws Exception {
                try {
                    Uri webXml = uri(context.getResource("/WEB-INF/web.xml"));
                    Uri url = webXml.path(hierarchicalPath(webXml.path()).parent().parent().toString());
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

}
