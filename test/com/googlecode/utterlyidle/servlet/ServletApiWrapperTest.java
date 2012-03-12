package com.googlecode.utterlyidle.servlet;

import com.googlecode.totallylazy.Xml;
import junit.framework.TestCase;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ServletApiWrapperTest {
    @Test
    public void canGetTheServletPathFromTheXml() throws Exception {
        Document document = Xml.document(WEB_XML);
        String path = ServletApiWrapper.servletUrl(document, ApplicationServlet.class);
        assertThat(path, is("/rest/"));
    }

    private final String WEB_XML = "<?xml version=\"1.0\"?>\n" +
            "<web-app xmlns=\"http://java.sun.com/xml/ns/j2ee\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd\"\n" +
            "         version=\"2.4\">" +
            "    <servlet>\n" +
            "        <servlet-name>ApplicationServlet</servlet-name>\n" +
            "        <servlet-class>com.googlecode.utterlyidle.servlet.ApplicationServlet</servlet-class>\n" +
            "        <init-param>\n" +
            "            <param-name>application</param-name>\n" +
            "            <param-value>com.example.RestApplication</param-value>\n" +
            "        </init-param>\n" +
            "    </servlet>\n" +
            "\n" +
            "    <servlet-mapping>\n" +
            "        <servlet-name>ApplicationServlet</servlet-name>\n" +
            "        <url-pattern>/rest/*</url-pattern>\n" +
            "    </servlet-mapping>\n" +
            "</web-app>";
}
