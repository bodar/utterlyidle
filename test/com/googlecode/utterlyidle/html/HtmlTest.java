package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.matchers.NumberMatcher;
import org.junit.Test;

import static com.googlecode.utterlyidle.html.Html.html;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class HtmlTest {
    @Test
    public void printsContentIfItCantParseIt() throws Exception {
        try {
            new Html("foo");
            fail("should have thrown");
        } catch (IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("foo"));
        }
    }

    @Test
    public void supportsCount() throws Exception{
        Html html = html("<html/>");
        assertThat(html.count("//html"), NumberMatcher.is(1));
        assertThat(html.count("//bob"), NumberMatcher.is(0));
    }

    @Test
    public void supportsContains() throws Exception{
        Html html = html("<html/>");
        assertThat(html.contains("//html"), is(true));
        assertThat(html.contains("//bob"), is(false));
    }
}
