package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Sequence;
import org.w3c.dom.Element;

import static com.googlecode.totallylazy.Predicates.equalTo;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Strings.empty;
import static com.googlecode.totallylazy.Xml.removeAttribute;

public class Radio implements NameValue {
    public static final String CHECKED = "checked";
    private final Sequence<Element> options;

    public Radio(Sequence<Element> options) {
        this.options = options;
    }

    public String name() {
        return options.headOption().map(attribute("name")).getOrElse((String)null);
    }

    public String value() {
        return options.filter(where(attribute(CHECKED), not(empty()))).headOption().map(attribute("value")).getOrElse((String)null);
    }

    public Radio value(String value) {
        options.each(removeAttribute(CHECKED));
        options.filter(where(attribute("value"),is(equalTo(value)))).forEach(setAttribute(CHECKED,CHECKED));
        return this;
    }

    public static Function1<Element, Element> setAttribute(final String name, final String value) {
        return new Function1<Element, Element>() {
            public Element call(Element element) throws Exception {
                element.setAttribute(name,value);
                return element;
            }
        };
    }

    public static Function1<Element, String> attribute(final String attributeName) {
        return new Function1<Element, String>() {
            public String call(Element element) throws Exception {
                return element.getAttribute(attributeName);
            }
        };
    }
}
