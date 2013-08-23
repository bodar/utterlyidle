package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.Xml;
import org.w3c.dom.Element;

import static com.googlecode.totallylazy.Xml.expectElement;
import static com.googlecode.totallylazy.Xml.removeAttribute;
import static com.googlecode.totallylazy.Xml.selectContents;
import static com.googlecode.totallylazy.Xml.selectElements;
import static java.lang.String.format;

public class Select implements NameValue {
    public static final String SELECTED = "selected";
    public static final String SELECTED_OPTION = "option[@selected='selected']";
    private final Element select;

    public Select(Element select) {
        this.select = select;
    }

    public Sequence<Option> options() {
        return Xml.selectNodes(select, "option").map(Option.asNode());
    }

    public String value() {
        String selected = selectContents(select, SELECTED_OPTION + "/@value");
        return selected.equals(Strings.EMPTY) ? selectContents(select, "option[1]/@value") : selected;
    }

    public String text() {
        String text = selectContents(select, SELECTED_OPTION + "/text()");
        return text.equals(Strings.EMPTY) ? selectContents(select, "option[1]/text()") : text;
    }

    public Select value(String value) {
        return valueWithXPath("option[@value='" + value + "']");
    }

    public Select valueWithText(String text) {
        return valueWithXPath("option[@text='" + text + "']");
    }

    public Select valueWithXPath(String optionExpression) {
        selectElements(select, SELECTED_OPTION).each(removeAttribute(SELECTED));
        expectElement(select, optionExpression).setAttribute(SELECTED, SELECTED);
        return this;
    }

    public String name() {
        return attribute("name");
    }

    public String attribute(String attributeName) {
        return selectContents(select, format("@%s", attributeName));
    }
}
