package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Xml;
import com.googlecode.utterlyidle.Response;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static com.googlecode.totallylazy.Xml.selectContents;
import static com.googlecode.totallylazy.Xml.selectElement;
import static java.lang.String.format;

public class Html {
    private final Document document;

    public Html(String document) {
        try {
            this.document = Xml.document(document);
        } catch (LazyException e){
            throw new IllegalArgumentException(format("Could not parse html: %s", document));
        }
    }

    public static Html html(Response response) throws Exception {
        return new Html(new String(response.bytes(), "UTF-8"));
    }

    public String title() {
        return Xml.selectContents(document, "/html/head/title");
    }

    public Form form(String xpath) {
        return new Form(selectElement(document, xpath).get());
    }

    public Input input(String xpath) {
        return new Input(selectElement(document, xpath).get());
    }

    public TextArea textarea(String xpath) {
        return new TextArea(selectElement(document, xpath).get());
    }

    public Select select(String xpath) {
        return new Select(selectElement(document, xpath).get());
    }

    public Checkbox checkbox(String xpath) {
        return new Checkbox(selectElement(document, xpath).get());
    }

    public String selectContent(String xpath) {
        return selectContents(document, xpath);
    }

    public Link link(String xpath) {
        return new Link(selectElement(document, xpath).get());
    }

    public Sequence<Node> nodes(String xpath) {
        return Xml.selectNodes(document, xpath);
    }
}
