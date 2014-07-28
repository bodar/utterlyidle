package com.googlecode.utterlyidle.html;

import org.w3c.dom.Element;

import static com.googlecode.totallylazy.Xml.selectContents;


public class TextArea implements NameValue {
    public static final String NAME = "@name";
    protected final Element textArea;

    public TextArea(Element textArea) {
        this.textArea = textArea;
    }

    public String value() {
        return textArea.getTextContent();
    }

    public TextArea value(String value) {
        textArea.setTextContent(value);
        return this;
    }

    public String name() {
        return selectContents(textArea, NAME);
    }
}
