package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.time.Dates;
import com.googlecode.totallylazy.time.FixedClock;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import static com.googlecode.utterlyidle.Responses.response;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DateHandlerTest {
    @Test
    public void addsDate() throws Exception {
        DateHandler handler = new DateHandler(ReturnResponseHandler.returnsResponse(response()), new FixedClock(Dates.date(2000, 1, 1)));
        Response response = handler.handle(RequestBuilder.get("/foo").build());

        assertThat(response.header(HttpHeaders.DATE), is("Sat, 01 Jan 2000 00:00:00 UTC"));
    }

    @Test
    public void neverOverridesDate() throws Exception{
        DateHandler handler = new DateHandler(ReturnResponseHandler.returnsResponse(response().header(HttpHeaders.DATE, "simon")), null);
        Response response = handler.handle(RequestBuilder.get("/foo").build());

        assertThat(response.header(HttpHeaders.DATE), is("simon"));
    }
}
