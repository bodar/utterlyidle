package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.xml.Xml;
import org.w3c.dom.Element;


public class Input extends AbstractElement implements NameValue {
    public static final String NAME = "name";
    public static final String VALUE = "value";

    public Input(Element input) {
        super(input);
    }

    public String value() {
        return attribute(VALUE);
    }

    public Input value(String value) {
        setAttribute("value",value);
        return this;
    }

    public String name() {
        return attribute(NAME);
    }

    public boolean enabled() {
        return !disabled();
    }

    public boolean disabled() {
        return Xml.matches(node, "boolean(@disabled='disabled')");
    }
}
