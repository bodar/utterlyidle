package com.googlecode.utterlyidle.servlet;

import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.http.Uri;
import com.googlecode.totallylazy.xml.Xml;
import com.googlecode.utterlyidle.AttributeMap;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.WebRoot;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.http.Uri.uri;
import static com.googlecode.utterlyidle.io.HierarchicalPath.hierarchicalPath;

public class ServletApiWrapper {
    public static final String WEB_XML = "/WEB-INF/web.xml";

    public static Callable<WebRoot> webRoot(final ServletContext context) {
        return () -> {
            try {
                Uri webXml = uri(context.getResource(WEB_XML));
                Uri url = webXml.path(hierarchicalPath(webXml.path()).parent().parent().toString());
                return WebRoot.webRoot(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static Callable<BasePath> basePath(final ServletContext context) {
        return () -> {
            Document document = Xml.document(Strings.toString(context.getResourceAsStream(WEB_XML)));
            String url = servletUrl(document, ApplicationServlet.class);
            String root = context.getContextPath();
            return BasePath.basePath(root + url);
        };
    }

    public static String servletUrl(Node node, Class<?> servletClass) {
        String servletName = Xml.selectContents(node, String.format("//servlet[servlet-class='%s']/servlet-name", servletClass.getName()));
        String urlPattern = Xml.selectContents(node, String.format("//servlet-mapping[servlet-name='%s']/url-pattern", servletName));
        return removeWildcard(urlPattern);
    }

    private static String removeWildcard(String urlPattern) {
        return urlPattern.replace("*", "");
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
