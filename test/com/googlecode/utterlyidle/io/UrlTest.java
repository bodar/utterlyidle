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
        String encodedValue = "http://myserver/resource?query=" + queryString;
        assertThat(url(encodedValue).replacePath(hierarchicalPath("/other/resource")).toString(), is("http://myserver/other/resource?query=" + queryString));
    }

    @Test
    public void shouldNotChangeUrl() throws UnsupportedEncodingException {
        assertThat(url("http://server/").toString(), is("http://server/"));
        assertThat(url("http://server/#fragment").toString(), is("http://server/#fragment"));
        assertThat(url("http://server/?query=").toString(), is("http://server/?query="));
        assertThat(url("http://dan:password@server/?query=").toString(), is("http://dan:password@server/?query="));
        assertThat(url("http://server:80/").toString(), is("http://server:80/"));
        assertThat(url("/foo").toString(), is("/foo"));
        assertThat(url("foo").toString(), is("foo"));
        assertThat(url("file:///foo/boo").toString(), is("file:///foo/boo"));
    }

    @Test
    public void shouldReturnRawQuery() throws UnsupportedEncodingException {
        String queryString = URLEncoder.encode("some?&urlencoded:value", "UTF-8");
        Url url = url("http://myserver/resource?query=" + queryString);

        assertThat(url.getQuery(), is("query="+queryString));
    }
}
