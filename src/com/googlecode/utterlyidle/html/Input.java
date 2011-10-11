package com.googlecode.utterlyidle.html;

import org.w3c.dom.Element;

import static com.googlecode.totallylazy.Xml.selectContents;
import static com.googlecode.totallylazy.Xml.selectNode;
import static java.lang.String.format;


public class Input implements NameValue{
    public static final String NAME = "@name";
    public static final String VALUE = "@value";
    protected final Element input;

    public Input(Element input) {
        this.input = input;
    }

    public String value() {
        return attribute(VALUE);
    }

    public Input value(String value) {
        selectNode(input, VALUE).setTextContent(value);
        return this;
    }

    public String name() {
        return attribute(NAME);
    }

    public String attribute(String attributeName) {
        return selectContents(input, format("@%s", attributeName));
    }
}
