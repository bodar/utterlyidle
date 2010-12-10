package com.googlecode.utterlyidle.io;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.googlecode.utterlyidle.io.HierarchicalPath.hierarchicalPath;
import static com.googlecode.utterlyidle.io.Url.url;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UrlTest {
    @Test
    public void shouldBeAbleToReplacePath() throws UnsupportedEncodingException {
        String queryString = URLEncoder.encode("some?&urlencoded:value", "UTF-8");
        Url url = url("http://myserver/resource?query=" + queryString);

        assertThat(url.replacePath(hierarchicalPath("/other/resource")).toString(), is("http://myserver/other/resource?query=" + queryString));
    }
}
