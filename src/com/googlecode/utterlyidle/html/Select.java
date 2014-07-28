package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Randoms;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.Xml;
import org.w3c.dom.Element;

import static com.googlecode.totallylazy.Xml.selectContents;
import static com.googlecode.totallylazy.Xml.selectElement;
import static com.googlecode.utterlyidle.html.OptionElement.functions.option;
import static java.lang.String.format;

public class Select extends AbstractElement implements NameValue {
    public static final String SELECTED = "selected";
    public static final String SELECTED_OPTION = "option[@selected='selected']";
    private final Element select;

    public Select(Element select) {
        super(select);
        this.select = select;
    }

    public Sequence<OptionElement> options() {
        return Xml.selectNodes(select, "option").map(option);
    }

    public Option<OptionElement> selected() {
        return selectElement(select, SELECTED_OPTION).map(option);
    }

    public String value() {
        return selected().getOrElse(options().get(0)).value();
    }

    public String text() {
        return selected().getOrElse(options().get(0)).text();
    }

    public Select value(String value) {
        return valueWithXPath("option[@value='" + value + "']");
    }

    public Select valueWithText(String text) {
        return valueWithXPath("option[text()=" + quote(text) + "]");
    }

    public Select valueByIndex(final int index) {
        return valueWithXPath("option[" + (index + 1) + "]");
    }

    public Select valueRandomly() {
        return valueByIndex(Randoms.between(0, size() - 1).head());
    }

    public int size() {
        return count("option").intValue();
    }

    public Select valueWithXPath(String optionExpression) {
        selectElements(SELECTED_OPTION).each(Xml.removeAttribute(SELECTED));
        expectElement(optionExpression).setAttribute(SELECTED, SELECTED);
        return this;
    }

    public String name() {
        return attribute("name");
    }

    public String attribute(String attributeName) {
        return selectContents(select, format("@%s", attributeName));
    }

    private String quote(final String text) {
        // Simple fix until we TL supports XPath variables
        return text.contains("'") ? "\"" + text + "\"" : "'" + text + "'";
    }

}
