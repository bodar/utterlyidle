package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Strings;
import org.w3c.dom.Element;

import static com.googlecode.totallylazy.Xml.removeAttribute;
import static com.googlecode.totallylazy.Xml.selectContents;
import static com.googlecode.totallylazy.Xml.selectElement;
import static com.googlecode.totallylazy.Xml.selectElements;

public class Select implements NameValue {
    public static final String SELECTED = "selected";
    public static final String SELECTED_OPTION = "option[@selected='selected']";
    private final Element select;

    public Select(Element select) {
        this.select = select;
    }

    public String value() {
        String selected = selectContents(select, SELECTED_OPTION + "/@value");
        return selected.equals(Strings.EMPTY) ? selectContents(select, "option[1]/@value") : selected;
    }

    public Select value(String value){
        selectElements(select, SELECTED_OPTION).each(removeAttribute(SELECTED));
        selectElement(select, "option[@value='" + value + "']").setAttribute(SELECTED, SELECTED);
        return this;
    }

    public String name() {
        return selectContents(select, "@name");
    }
}