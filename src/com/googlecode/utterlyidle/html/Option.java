package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;
import org.w3c.dom.Node;

import static com.googlecode.totallylazy.Xml.selectContents;

public class Option {

    private Node node;

    public Option(Node node) {
        this.node = node;
    }

    public static Callable1<Node, Option> asNode() {
        return new Callable1<Node, Option>() {
            @Override
            public Option call(Node node) throws Exception {
                return new Option(node);
            }
        };
    }

    public static Callable1<Option, Pair<String, String>> asEntry() {
        return new Callable1<Option, Pair<String, String>>() {
            @Override
            public Pair<String, String> call(Option option) throws Exception {
                return option.entry();
            }
        };
    }

    public static Callable1<Option, String> asValue() {
        return new Callable1<Option, String>() {
            @Override
            public String call(Option option) throws Exception {
                return option.value();
            }
        };
    }

    public static Callable1<Option, String> asText() {
        return new Callable1<Option, String>() {
            @Override
            public String call(Option option) throws Exception {
                return option.text();
            }
        };
    }

    public String value() {
        return selectContents(node, "@value");
    }

    public String text() {
        return selectContents(node, "text()");
    }

    public Pair<String, String> entry() {
        return Pair.pair(value(), text());
    }


}
