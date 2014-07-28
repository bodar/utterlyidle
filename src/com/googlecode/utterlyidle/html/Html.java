package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Xml;
import com.googlecode.utterlyidle.Response;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static java.lang.String.format;

public class Html extends BlockLevelElement {
    public Html(Node node) {
        super(node);
    }

    public static Html html(String html) throws Exception {
        return new Html(parse(html));
    }

    public static Html html(Response response) throws Exception {
        return html(response.entity().toString());
    }

    private static Document parse(String html) {
        try {
            return Xml.document(html);
        } catch (LazyException e) {
            Throwable cause = e.getCause();
            throw new IllegalArgumentException(format("Could not parse html because: %s\n%s", cause.getMessage(), html), cause);
        }
    }

    public String title() {
        return Xml.selectContents(node, "/html/head/title");
    }


    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Html))
            return false;
        Html html = (Html) object;
        try {
            return Xml.format(this.node).equals(Xml.format(html.node));
        } catch (Exception e) {
            return false;
        }
    }

    public Sequence<String> selectValues(String xpath) {
        return Xml.textContents(Xml.selectNodes(node, xpath));
    }

}
