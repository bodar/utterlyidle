package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Sequence;
import org.w3c.dom.Element;

import java.util.NoSuchElementException;

import static com.googlecode.totallylazy.predicates.Predicates.not;
import static com.googlecode.totallylazy.predicates.Predicates.where;
import static com.googlecode.totallylazy.Strings.empty;
import static com.googlecode.totallylazy.xml.Xml.functions.attribute;
import static com.googlecode.totallylazy.xml.Xml.functions.matches;
import static com.googlecode.totallylazy.xml.Xml.functions.setAttribute;
import static com.googlecode.totallylazy.xml.Xml.removeAttribute;
import static java.lang.String.format;

public class Radio implements NameValue {
    public static final String CHECKED = "checked";
    private final Sequence<Element> options;
    private final AbstractElement ancestor;
    private final String xpath;

    public Radio(AbstractElement ancestor, String xpath) {
        this.ancestor = ancestor;
        this.xpath = xpath;
        options = ancestor.selectElements(xpath);
    }

    public String name() {
        return options.headOption().map(attribute("name")).getOrElse((String)null);
    }

    public String value() {
        return options.filter(where(attribute(CHECKED), not(empty()))).headOption().map(attribute("value")).getOrElse((String)null);
    }

    public Radio value(String value) {
        return valueWithXPath("self::*[@value='" + value + "']");
    }


    /**
     * Input expression with be applied to each <input/>
     *
     * So to match this input:
     *
     * <input type="radio" value="some value"/><span>Some text</span>
     *
     * You would do this:
     *
     * ""self::*[following-sibling::span[1][text()='Some text']]""
     * @param inputExpression
     * @return
     */
    public Radio valueWithXPath(String inputExpression) {
        if(options.isEmpty())
            throw new NoSuchElementException(format("No elements matched xpath %s inside %s", xpath, ancestor));

        Sequence<Element> matchingOptions = options.filter(matches(inputExpression)).realise();
        if(matchingOptions.isEmpty())
            throw new NoSuchElementException(format("No radio input matches '%s'", inputExpression));

        clearValue();
        matchingOptions.each(setAttribute(CHECKED, CHECKED));
        return this;
    }

    public void clearValue() {
        options.each(removeAttribute(CHECKED));
    }

}
