package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Mapper;
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
        public static Mapper<Node, OptionElement> option = new Mapper<Node, OptionElement>() {
            @Override
            public OptionElement call(Node node) throws Exception {
                return new OptionElement(node);
            }
        };

        public static Mapper<OptionElement, Pair<String, String>> pair = new Mapper<OptionElement, Pair<String, String>>() {
            @Override
            public Pair<String, String> call(OptionElement optionElement) throws Exception {
                return optionElement.pair();
            }
        };

        public static Mapper<OptionElement, String> value = new Mapper<OptionElement, String>() {
            @Override
            public String call(OptionElement optionElement) throws Exception {
                return optionElement.value();
            }
        };

        public static Mapper<OptionElement, String> text = new Mapper<OptionElement, String>() {
            @Override
            public String call(OptionElement optionElement) throws Exception {
                return optionElement.text();
            }
        };
    }
}
