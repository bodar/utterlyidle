package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Exceptions;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Xml;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static com.googlecode.totallylazy.Xml.selectContents;
import static com.googlecode.utterlyidle.html.Html.html;
import static java.lang.String.format;

public class AbstractElement {
    protected final Node node;

    public AbstractElement(Node node) {
        this.node = node;
    }

    public boolean hasAttribute(String attributeName) {
        return Xml.matches(node, format("@%s", attributeName));
    }

    public String attribute(String attributeName) {
        return Xml.selectContents(node, format("@%s", attributeName));
    }

    public void setAttribute(String name, String value) {
        if(node instanceof Element)
            ((Element) node).setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        if(node instanceof Element)
            ((Element) node).removeAttribute(name);
    }

    public String contents(String expression){
        return selectContents(node, expression);
    }

    public Number count(String xpath) {
        return Xml.selectNumber(node, String.format("count(%s)", xpath));
    }

    public String selectContent(String xpath) {
        return selectContents(node, xpath);
    }

    protected Element expectElement(String xpath) {
        return Xml.expectElement(node, xpath);
    }

    protected Sequence<Element> selectElements(String xpath) {
        return Xml.selectElements(node, xpath);
    }

    public boolean contains(String xpath) {
        return Xml.matches(node, xpath);
    }

    public Html innerHtml(String xpath) throws Exception {
        return html(selectContents(node, xpath));
    }

    @Override
    public String toString() {
        try {
            return Xml.format(node);
        } catch (Exception e) {
            return "Exception in toString():\n" + Exceptions.asString(e);
        }
    }
    public String toString(Pair<String, Object>... attributes) {
        try {
            return Xml.format(node, attributes);
        } catch (Exception e) {
            return "Exception in toString():\n" + Exceptions.asString(e);
        }
    }
}
