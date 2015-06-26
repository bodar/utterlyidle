package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.googlecode.utterlyidle.html.TableCell.functions.toTableCell;
import static java.lang.String.format;

public class TableRow extends AbstractElement {
    private final Map<String, Integer> columnNames;

    public TableRow(Element element, Map<String, Integer> columnNames) {
        super(element);
        this.columnNames = columnNames;
    }

    public TableCell get(String columnName){
        if(!columnNames.containsKey(columnName))
            throw new NoSuchElementException(format("Column '%s' not found. Column names are:%s",
                    columnName,
                    Sequences.sequence(columnNames.keySet()).toString("\n")));
        return cells().get(columnNames.get(columnName));
    }

    public Sequence<TableCell> cells(){
        return selectElements("descendant::th | td").map(toTableCell());
    }

    public static class functions{
        public static Function1<Element, TableRow> toTableRow(){
            return toTableRow(new HashMap<String, Integer>());
        }

        public static Function1<Element, TableRow> toTableRow(final Map<String, Integer> columnNames){
            return element -> new TableRow(element, columnNames);
        }
    }
}
