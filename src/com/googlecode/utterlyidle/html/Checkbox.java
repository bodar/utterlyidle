package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Xml;
import org.w3c.dom.Element;

public class Checkbox extends Input{
    public static final String CHECKED = "checked";

    public Checkbox(Element input) {
        super(input);
    }

    public boolean checked() {
        return Xml.selectContents(input, "@checked").equals(CHECKED);
    }

    public Checkbox check() {
        input.setAttribute(CHECKED, CHECKED);
        return this;
    }

    public Checkbox uncheck() {
        input.removeAttribute(CHECKED);
        return this;
    }
}
