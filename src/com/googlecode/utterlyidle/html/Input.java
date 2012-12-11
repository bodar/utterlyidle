package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Xml;
import org.w3c.dom.Element;

import static com.googlecode.totallylazy.Xml.selectContents;
import static java.lang.String.format;


public class Input implements NameValue{
    public static final String NAME = "@name";
    public static final String VALUE = "@value";
    protected final Element input;

    public Input(Element input) {
        this.input = input;
    }

    public String value() {
        return selectContents(input, VALUE);
    }

    public Input value(String value) {
        input.setAttribute("value",value);
        return this;
    }

    public String name() {
        return selectContents(input, NAME);
    }

    public String attribute(String attributeName) {
        return selectContents(input, format("@%s", attributeName));
    }

    public boolean enabled() {
        return !disabled();
    }

    public boolean disabled() {
        return Xml.matches(input, "boolean(@disabled='disabled')");
    }
}
