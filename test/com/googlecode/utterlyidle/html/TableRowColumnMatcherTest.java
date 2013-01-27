package com.googlecode.utterlyidle.html;

import org.junit.Test;

import static com.googlecode.utterlyidle.html.ElementContentsMatcher.elementContents;
import static com.googlecode.utterlyidle.html.Html.html;
import static com.googlecode.utterlyidle.html.TableRowColumnMatcher.columnValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TableRowColumnMatcherTest {
    @Test
    public void shouldMatchByRowPredicate() throws Exception {
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

        assertThat(table.bodyRows().first(), columnValue("Column 1", elementContents(is("R1 C1"))));
    }
}
