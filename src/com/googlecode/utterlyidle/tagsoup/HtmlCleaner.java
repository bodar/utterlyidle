package com.googlecode.utterlyidle.tagsoup;

import com.googlecode.totallylazy.LazyException;
import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class HtmlCleaner {
    public static Node node(String html) {
        return node(new ByteArrayInputStream(html.getBytes()));
    }

    public static Node node(InputStream stream) {
        try {
            DOMResult result = new DOMResult();
            transformer().transform(new SAXSource(parser(), new InputSource(stream)), result);
            return result.getNode();
        } catch (Exception e) {
            throw LazyException.lazyException(e);
        }
    }

    public static Document document(String html) {
        return (Document) HtmlCleaner.node(html);
    }

    public static Document document(InputStream stream) {
        return (Document) node(stream);
    }

    private static Transformer transformer() throws TransformerConfigurationException {
        return TransformerFactory.newInstance().newTransformer();
    }

    private static XMLReader parser() throws Exception {
        return new Parser(){{
                    setFeature(Parser.namespacesFeature, false);
                    setFeature(Parser.namespacePrefixesFeature, false);
                }};
    }
}