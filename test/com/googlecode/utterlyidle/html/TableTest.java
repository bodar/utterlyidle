package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Sequence;
import org.junit.Test;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.html.Html.html;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TableTest {
    @Test
    public void supportsRowsOutsideHeaderOrFooter() throws Exception{
        Html html = html("<html>" +
                "<table>" +
                "   <tr>" +
                "      <th>Column 1</th>" +
                "      <th>Column 2</th>" +
                "   </tr>" +
                "   <tr><td>R1 C1</td><td>R1 C2</td></tr>" +
                "   <tr><td>R2 C1</td><td>R2 C2</td></tr>" +
                "</table></html>");

        Table table = html.table("//table");
        assertThat(table.headerRows().size(), is(equalTo(0)));
        assertThat(table.bodyRows().size(), is(equalTo(0)));
        assertThat(table.allRows().size(), is(equalTo(3)));

        assertThat(table.allRows().first().cells().first().value(), is("Column 1"));
    }

    @Test
    public void supportsHeaderAndBodyRows() throws Exception{
        Html html = html("<html>" +
                "<table>" +
                "   <thead>" +
                "      <tr>" +
                "         <th>Column 1</th>" +
                "         <th>Column 2</th>" +
                "      </tr>" +
                "   </thead>" +
                "   <tbody>" +
                "      <tr><td>R1 C1</td><td>R1 C2</td></tr>" +
                "      <tr><td>R2 C1</td><td>R2 C2</td></tr>" +
                "   </tbody>" +
                "</table></html>");

        Table table = html.table("//table");
        assertThat(table.headerRows().size(), is(equalTo(1)));
        assertThat(table.bodyRows().size(), is(equalTo(2)));
        assertThat(table.allRows().size(), is(equalTo(3)));

        TableRow headerRow = table.headerRows().first();

        assertThat(headerRow.cells().size(), is(2));
        assertThat(headerRow.cells().first().value(), is("Column 1"));
        assertThat(headerRow.cells().first().isHeader(), is(true));
        assertThat(headerRow.cells().second().value(), is("Column 2"));

        Sequence<TableCell> firstRow = table.bodyRows().first().cells();

        assertThat(firstRow.size(), is(2));
        assertThat(firstRow.first().value(), is("R1 C1"));
        assertThat(firstRow.first().isHeader(), is(false));
        assertThat(firstRow.second().value(), is("R1 C2"));

        Sequence<TableCell> secondRow = table.bodyRows().second().cells();

        assertThat(secondRow.size(), is(2));
        assertThat(secondRow.first().value(), is("R2 C1"));
        assertThat(secondRow.second().value(), is("R2 C2"));
    }

    @Test
    public void readsColumnNamesFromFirstHeaderRow() throws Exception{
        Html html = html("<html>" +
                "<table>" +
                "   <thead>" +
                "      <tr>" +
                "         <th>Column 1</th>" +
                "         <th>Column 2</th>" +
                "      </tr>" +
                "   </thead>" +
                "   <tbody>" +
                "      <tr><td>R1 C1</td><td>R1 C2</td></tr>" +
                "      <tr><td>R2 C1</td><td>R2 C2</td></tr>" +
                "   </tbody>" +
                "</table></html>");

        Table table = html.table("//table");
        assertThat(table.bodyRows().first().get("Column 1").value(), is("R1 C1"));
        assertThat(table.bodyRows().first().get("Column 2").value(), is("R1 C2"));
    }


    @Test
    public void supportsUserDefinedColumnNames() throws Exception{
        Html html = html("<html>" +
                "<table>" +
                "   <thead>" +
                "      <tr>" +
                "         <th>Column 1</th>" +
                "         <th>Column 2</th>" +
                "      </tr>" +
                "   </thead>" +
                "   <tbody>" +
                "      <tr><td>R1 C1</td><td>R1 C2</td></tr>" +
                "      <tr><td>R2 C1</td><td>R2 C2</td></tr>" +
                "   </tbody>" +
                "</table></html>");

        Table table = html.table("//table", sequence("Avocado", "Pear"));
        assertThat(table.bodyRows().first().get("Avocado").value(), is("R1 C1"));
        assertThat(table.bodyRows().first().get("Pear").value(), is("R1 C2"));
    }


    @Test
    public void supportsScrapingColumnNamesFromHeaderUsingXpath() throws Exception{
        Html html = html("<html>" +
                "<table>" +
                "   <thead>" +
                "      <tr>" +
                "         <th><a>Column 1</a> <a>sort</a></th>" +
                "         <th><a>Column 2</a> <a>sort</a></th>" +
                "      </tr>" +
                "   </thead>" +
                "   <tbody>" +
                "      <tr><td>R1 C1</td><td>R1 C2</td></tr>" +
                "      <tr><td>R2 C1</td><td>R2 C2</td></tr>" +
                "   </tbody>" +
                "</table></html>");

        Table table = html.table("//table", "descendant::*[1]");
        assertThat(table.bodyRows().first().get("Column 1").value(), is("R1 C1"));
        assertThat(table.bodyRows().first().get("Column 2").value(), is("R1 C2"));
    }
}
