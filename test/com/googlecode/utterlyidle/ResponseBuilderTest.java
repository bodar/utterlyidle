package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.time.Dates;
import org.junit.Test;

import java.util.Date;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.matchers.IterableMatcher.hasExactly;
import static com.googlecode.utterlyidle.HttpHeaders.LAST_MODIFIED;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResponseBuilderTest {
    @Test
    public void supportsAddedingAHeader() throws Exception {
        Date date = Dates.date(2001, 1, 1);
        Response response = response().header(LAST_MODIFIED, date).build();
        assertThat(response.headers(), hasExactly(pair(LAST_MODIFIED, Dates.RFC822().format(date))));
    }
}
