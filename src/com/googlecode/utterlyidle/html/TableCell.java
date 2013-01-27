package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Value;
import org.w3c.dom.Element;

public class TableCell extends ContainerElement implements Value<String> {

    public TableCell(Element element) {
        super(element);
    }

    public boolean isHeader(){
        return node.getNodeName().toLowerCase().equals("th");
    }

    public String value(){
        return node.getTextContent();
    }

    public static class functions{
        public static Function1<Element, TableCell> toTableCell(){
            return new Function1<Element, TableCell>() {
                @Override
                public TableCell call(Element element) throws Exception {
                    return new TableCell(element);
                }
            };
        }
    }
}
