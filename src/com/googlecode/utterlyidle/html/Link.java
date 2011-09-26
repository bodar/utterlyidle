package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Xml;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import org.w3c.dom.Element;

public class Link implements NameValue{
    private final Element link;

    public Link(Element link) {
        this.link = link;
    }

    public Request click() {
        return RequestBuilder.get(value()).build();
    }

    public String value() {
        return Xml.selectContents(link, "@href");
    }

    public String name() {
        return link.getTextContent();
    }
}
