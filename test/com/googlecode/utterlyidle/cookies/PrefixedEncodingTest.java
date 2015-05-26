package com.googlecode.utterlyidle.cookies;

import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PrefixedEncodingTest extends CookieEncodingContract {

    @Override
    protected String input() {
        return "name=value";
    }

    @Override
    protected String expectedOutput() {
        return "utterlyidle:v1:name=value";
    }

    @Override
    protected CookieEncoding encoding() {
        return new PrefixedEncoding(new IdentityEncoding());
    }

    @Test
    public void returnsCookieUnchangedIfItIsNotPrefixed() throws Exception {
        assertThat(new PrefixedEncoding(new Base64Encoding()).decode("no prefix"), is("no prefix"));
    }
}