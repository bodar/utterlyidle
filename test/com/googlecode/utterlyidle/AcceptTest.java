package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.matchers.NumberMatcher;
import org.junit.Test;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.Accept.accept;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AcceptTest {
    @Test
    public void canParseRealWorldAccepts() {
        Accept accept = accept("application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");

        assertThat(accept.matches("text/html"), is(true));
        assertThat(accept.quality("text/html"), NumberMatcher.is(0.9f));
    }

    @Test
    public void canParseMultipleAcceptsHeaders() {
        Accept accept = accept("application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");

        assertThat(accept.matches("text/html"), is(true));
        assertThat(accept.quality("text/html"), NumberMatcher.is(0.9f));
    }

    @Test
    public void canHandleWildcard() {
        Accept accept = accept("application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");

        assertThat(accept.matches("*/*"), is(true));
        assertThat(accept.quality("*/*"), NumberMatcher.is(1.0f));
        // If the resource produces all mime types then the quality will be the highest out of the set
    }

    @Test
    public void canHandleWildcardWithExtraSpaceAndNoLeadingZero() {
        Accept accept = accept("text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");

        assertThat(accept.matches("*/*"), is(true));
        assertThat(accept.quality("*/*"), NumberMatcher.is(1.0f));
    }

    @Test
    public void wildcardWillMatch() {
        Accept accept = accept("text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");

        assertThat(accept.matches("application/xml"), is(true));
        assertThat(accept.quality("application/xml"), NumberMatcher.is(0.2f));
    }

    @Test
    public void canHandleSubtypeWildcard() {
        Accept accept = accept("image/png");

        assertThat(accept.matches("image/*"), is(true));
    }

    @Test
    public void subtypeWildcardWillMatch() {
        Accept accept = accept("image/*");

        assertThat(accept.matches("image/png"), is(true));
    }

    @Test
    public void subtypeWildcardWillMatchFullMimeType() throws Exception {
        Accept accept = accept("image/*");

        assertThat(accept.bestMatch(sequence("image/png")), is("image/png"));
    }

}