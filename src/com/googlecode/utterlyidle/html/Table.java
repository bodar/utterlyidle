package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.xml.Xml;
import org.w3c.dom.Element;

import java.util.Map;

import static com.googlecode.totallylazy.Maps.map;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.numbers.Numbers.range;
import static com.googlecode.totallylazy.xml.Xml.functions.selectContents;
import static com.googlecode.utterlyidle.html.TableRow.functions.toTableRow;

public class Table extends BlockLevelElement {
    private final Sequence<TableRow> allRows;
    private final Sequence<TableRow> bodyRows;
    private final Sequence<TableRow> headerRows;

    public Table(Element table) {
        this(table, ".");
    }

    public Table(Element table, String thContentsXpath) {
        this(
                table,
                Xml.selectElements(table, "descendant::thead/descendant::tr/descendant::th").
                        map(selectContents(thContentsXpath)));
    }

    public Table(Element table, Iterable<String> columnNames) {
        this(table, map(sequence(columnNames).zip(range(0).safeCast(Integer.class))));
    }

    public Table(Element table, Map<String, Integer> columnNames) {
        super(table);

        allRows = Xml.selectElements(table, "descendant::tr").map(toTableRow(columnNames)).memorise();
        bodyRows = Xml.selectElements(table, "descendant::tbody/descendant::tr").map(toTableRow(columnNames)).memorise();
        headerRows = Xml.selectElements(table, "descendant::thead/descendant::tr").map(toTableRow()).memorise();
    }

    public Sequence<TableRow> allRows() {
        return allRows;
    }

    public Sequence<TableRow> bodyRows() {
        return bodyRows;
    }

    public Sequence<TableRow> headerRows() {
        return headerRows;
    }
}
