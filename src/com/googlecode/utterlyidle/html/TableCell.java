package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Value;
import org.w3c.dom.Element;

public class TableCell extends BlockLevelElement implements Value<String> {

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
            return TableCell::new;
        }
    }
}
