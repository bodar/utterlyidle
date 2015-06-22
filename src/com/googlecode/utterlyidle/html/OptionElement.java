package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Pair;
import org.w3c.dom.Node;

public class OptionElement extends AbstractElement {
    public OptionElement(Node node) {
        super(node);
    }

    public String value() {
        return hasAttribute("value") ? attribute("value") : text();
    }

    public String text() {
        return selectContent("text()");
    }

    public Pair<String, String> pair() {
        return Pair.pair(text(), value());
    }

    public static class functions {
        public static Function1<Node, OptionElement> option = OptionElement::new;

        public static Function1<OptionElement, Pair<String, String>> pair = OptionElement::pair;

        public static Function1<OptionElement, String> value = OptionElement::value;

        public static Function1<OptionElement, String> text = OptionElement::text;
    }
}
