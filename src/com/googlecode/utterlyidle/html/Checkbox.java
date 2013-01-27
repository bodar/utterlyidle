package com.googlecode.utterlyidle.html;

import org.w3c.dom.Element;

public class Checkbox extends Input {
    public static final String CHECKED = "checked";

    public Checkbox(Element input) {
        super(input);
    }

    public boolean checked() {
        return selectContent("@checked").equals(CHECKED);
    }

    public Checkbox check() {
        setAttribute(CHECKED, CHECKED);
        return this;
    }

    public Checkbox uncheck() {
        removeAttribute(CHECKED);
        return this;
    }
}
