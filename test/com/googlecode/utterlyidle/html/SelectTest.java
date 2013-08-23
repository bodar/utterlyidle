package com.googlecode.utterlyidle.html;

import org.junit.Test;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.matchers.IterableMatcher.hasExactly;
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
