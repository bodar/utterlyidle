package com.googlecode.utterlyidle.html;

import com.googlecode.utterlyidle.Request;
import org.w3c.dom.Element;

import static com.googlecode.utterlyidle.Request.Builder.get;

public class Link extends AbstractElement implements NameValue {
    public Link(Element link) {
        super(link);
    }

    public Request click() {
        return get(value());
    }

    public String value() {
        return attribute("href");
    }

    public String name() {
        return selectContent("text()");
    }
}
