package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.time.Dates;
import com.googlecode.totallylazy.time.StoppedClock;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import org.junit.Test;

import static com.googlecode.utterlyidle.HttpHeaders.DATE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DateHandlerTest {
    @Test
    public void addsDate() throws Exception {
        DateHandler handler = new DateHandler(ReturnResponseHandler.returnsResponse(Response.ok()), new StoppedClock(Dates.date(2000, 1, 1)));
        Response response = handler.handle(Request.get("/foo"));

        assertThat(response.header(DATE).get(), is("Sat, 01 Jan 2000 00:00:00 GMT"));
    }

    @Test
    public void neverOverridesDate() throws Exception {
        DateHandler handler = new DateHandler(ReturnResponseHandler.returnsResponse(Response.response(Status.OK).header(DATE, "simon")), null);
        Response response = handler.handle(Request.get("/foo"));

        assertThat(response.header(DATE).get(), is("simon"));
    }
}
