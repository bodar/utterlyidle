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
        public static Function1<Node, OptionElement> option = new Function1<Node, OptionElement>() {
            @Override
            public OptionElement call(Node node) throws Exception {
                return new OptionElement(node);
            }
        };

        public static Function1<OptionElement, Pair<String, String>> pair = new Function1<OptionElement, Pair<String, String>>() {
            @Override
            public Pair<String, String> call(OptionElement optionElement) throws Exception {
                return optionElement.pair();
            }
        };

        public static Function1<OptionElement, String> value = new Function1<OptionElement, String>() {
            @Override
            public String call(OptionElement optionElement) throws Exception {
                return optionElement.value();
            }
        };

        public static Function1<OptionElement, String> text = new Function1<OptionElement, String>() {
            @Override
            public String call(OptionElement optionElement) throws Exception {
                return optionElement.text();
            }
        };
    }
}
