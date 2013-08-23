package com.googlecode.utterlyidle.html;

import org.junit.Test;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.matchers.IterableMatcher.hasExactly;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.html.Html.html;
import static org.hamcrest.MatcherAssert.assertThat;

public class SelectTest {
    @Test
    public void whenOptionHasNoValueAttributeUseText() throws Exception {
        Html html = html("<select>" +
                "<option>First</option>" +
                "<option value=\"\">Second</option>" +
                "</select>");
        Select select = html.select("//select");
        assertThat(select.options().map(Option.asEntry()),
                hasExactly(pair("First", "First"), pair("", "Second")));
    }

    @Test
    public void canSelectByText() throws Exception {
        Html html = html("<select>" +
                "<option value=\"Foo\">First</option>" +
                "</select>");
        Select select = html.select("//select");
        assertThat(select.valueWithText("First").value(), is("Foo"));
    }

    @Test
    public void canSelectByTextWithQuotes() throws Exception {
        Html html = html("<select>" +
                "<option value=\"Foo\">First ' Dan</option>" +
                "<option value=\"Bar\">Second \" Dan</option>" +
                "</select>");
        Select select = html.select("//select");
        assertThat(select.valueWithText("First ' Dan").value(), is("Foo"));
        assertThat(select.valueWithText("Second \" Dan").value(), is("Bar"));
    }

    @Test
    public void canSelectByIndex() throws Exception {
        Html html = html("<select>" +
                "<option value=\"Foo\">First</option>" +
                "<option value=\"Bar\">Second</option>" +
                "</select>");
        Select select = html.select("//select");
        assertThat(select.valueByIndex(0).value(), is("Foo"));
        assertThat(select.valueByIndex(1).value(), is("Bar"));
    }


    @Test
    public void supportsEntries() throws Exception {
        Html html = html("<select>" +
                "<option value=\"first\">First</option>" +
                "<option value=\"second\">Second</option>" +
                "</select>");
        Select select = html.select("//select");

        assertThat(select.options().map(Option.asEntry()),
                hasExactly(pair("first", "First"), pair("second", "Second")));
    }

}
